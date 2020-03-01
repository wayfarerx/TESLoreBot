package net.wayfarerx.teslore
package model

import java.io.InputStream

import cats.Defer
import cats.effect.Bracket
import cats.implicits._

import opennlp.tools.namefind.{NameFinderME, TokenNameFinder, TokenNameFinderModel}
import opennlp.tools.postag.{POSModel, POSTagger, POSTaggerME}
import opennlp.tools.sentdetect.{SentenceDetector, SentenceDetectorME, SentenceModel}
import opennlp.tools.tokenize.{Tokenizer, TokenizerME, TokenizerModel}
import opennlp.tools.util.Span

/**
 * A utility for extracting stories from scraped Imperial Library books.
 *
 * @tparam F The type of effect this interpreter uses.
 * @param sentenceDetector The sentence detector to use.
 * @param tokenizer        The tokenizer to use.
 * @param nameFinder       The name finder to use.
 * @param posTagger        The part-of-speech tagger to use.
 * @param bracket          The implicit bracket type class.
 * @param defer            The implicit defer type class.
 */
case class Interpreter[F[_]](
  sentenceDetector: SentenceDetector,
  tokenizer: Tokenizer,
  nameFinder: TokenNameFinder,
  posTagger: POSTagger
)(implicit
  bracket: Bracket[F, Throwable],
  defer: Defer[F]
) {

  import Interpreter.attempt

  /**
   * Attempts to interpret a story from the specified scrape.
   *
   * @param scrape The scrape to interpret.
   * @return The result of attempting to interpret a story from the specified scrape.
   */
  def interpret(scrape: library.Scrape): F[Story] =
    (interpretTitle(scrape.title), interpretAuthor(scrape.author), interpretParagraphs(scrape.paragraphs)) mapN Story

  /**
   * Attempts to interpret a title from the specified string.
   *
   * @param title The title to interpret.
   * @return The result of attempting to interpret a title from the specified string.
   */
  private def interpretTitle(title: String): F[Title] =
    interpretSentence(title) map (t => Title(t.elements))

  /**
   * Attempts to interpret an author from the specified string.
   *
   * @param author The author to interpret.
   * @return The result of attempting to interpret an author from the specified string.
   */
  private def interpretAuthor(author: String): F[Author] =
    tokens(author) map (a => Author(Element.Name(a.toList)))

  def interpretParagraphs(remaining: List[String]): F[List[Paragraph]] =
    remaining match {
      case head :: tail => (interpretParagraph(head), interpretParagraphs(tail)) mapN (_ :: _)
      case Nil => bracket.pure(Nil)
    }

  /**
   * Attempts to interpret a paragraph from the specified string.
   *
   * @param paragraph The paragraph to interpret.
   * @return The result of attempting to interpret a paragraph from the specified string.
   */
  private def interpretParagraph(paragraph: String): F[Paragraph] = {

    def interpretSentences(remaining: List[String]): F[List[Sentence]] =
      remaining match {
        case head :: tail => (interpretSentence(head), interpretSentences(tail)) mapN (_ :: _)
        case Nil => bracket.pure(Nil)
      }

    sentences(paragraph) flatMap (s => interpretSentences(s.toList)) map Paragraph
  }

  /**
   * Attempts to interpret a sentence from the specified string.
   *
   * @param sentence The sentence to interpret.
   * @return The result of attempting to interpret a sentence from the specified string.
   */
  def interpretSentence(sentence: String): F[Sentence] = {

    def continue(tokens: List[String], pos: List[String], names: List[Span], index: Int): List[Element] =
      tokens match {
        case tokensHead :: tokensTail =>
          val posHead :: posTail = pos
          names match {
            case namesHead :: namesTail if namesHead.getStart == index =>
              Element.Name(tokensHead :: tokensTail.take(namesHead.length - 1)) :: continue(
                tokensTail drop namesHead.length - 1,
                posTail drop namesHead.length - 1,
                namesTail,
                index + namesHead.length
              )
            case _ =>
              Element.Word(tokensHead, posHead) :: continue(tokensTail, posTail, names, index + 1)
          }
        case _ => Nil
      }

    for {
      _tokens <- tokens(sentence)
      _names <- names(_tokens)
      _pos <- pos(_tokens)
    } yield Sentence(continue(_tokens.toList, _pos.toList, _names.toList, 0))
  }

  /**
   * Attempts to invoke the raw sentence detector.
   *
   * @param text The text to split into sentences.
   * @return The result of attempting to invoke the raw sentence detector.
   */
  private def sentences(text: String): F[Array[String]] =
    attempt(sentenceDetector.sentDetect(text))

  /**
   * Attempts to invoke the raw token detector.
   *
   * @param text The text to split into tokens.
   * @return The result of attempting to invoke the raw token detector.
   */
  private def tokens(text: String): F[Array[String]] =
    attempt(tokenizer.tokenize(text))

  /**
   * Attempts to invoke the raw name detector.
   *
   * @param tokens The tokens to detect names in.
   * @return The result of attempting to invoke the raw name detector.
   */
  private def names(tokens: Array[String]): F[Array[Span]] =
    attempt(nameFinder.find(tokens)) // TODO Train the name finder.

  /**
   * Attempts to invoke the part-of-speech tagger.
   *
   * @param tokens The tokens to assign parts of speech to.
   * @return The result of attempting to invoke the part-of-speech tagger.
   */
  private def pos(tokens: Array[String]): F[Array[String]] =
    attempt(posTagger.tag(tokens))

}

/**
 * Factory for common Imperial Library book interpreters.
 */
object Interpreter {

  /**
   * Attempts to create a common interpreter.
   *
   * @tparam F The type of effect to use.
   * @param bracket The implicit bracket type class.
   * @param defer   The implicit defer type class.
   * @return The result of attempting to create a common interpreter.
   */
  def apply[F[_]]()(implicit bracket: Bracket[F, Throwable], defer: Defer[F]): F[Interpreter[F]] = for {
    sentenceDetector <- load("en-sentences.bin", stream => new SentenceDetectorME(new SentenceModel(stream)))
    tokenizer <- load("en-tokens.bin", stream => new TokenizerME(new TokenizerModel(stream)))
    nameFinder <- load("en-names.bin", stream => new NameFinderME(new TokenNameFinderModel(stream)))
    posTagger <- load("en-pos.bin", stream => new POSTaggerME(new POSModel(stream)))
  } yield Interpreter(sentenceDetector, tokenizer, nameFinder, posTagger)

  /**
   * Attempts to load the content of a resource.
   *
   * @tparam F The type of effect to use.
   * @tparam A The type of content to load.
   * @param path    The path to load the content from.
   * @param action  The action that imports the content.
   * @param bracket The implicit bracket type class.
   * @param defer   The implicit defer type class.
   * @return The result of attempting to load the content of a resource.
   */
  @inline private def load[F[_], A](path: String, action: InputStream => A)
    (implicit bracket: Bracket[F, Throwable], defer: Defer[F]): F[A] =
    bracket.bracket(attempt(getClass.getResourceAsStream(path)))(s => attempt(action(s)))(s => attempt(s.close()))


  /**
   * Attempts to perform an action.
   *
   * @tparam F The type of effect to use.
   * @tparam A The result of the action to perform.
   * @param f       The action to perform.
   * @param bracket The implicit bracket type class.
   * @param defer   The implicit defer type class.
   * @return The result of attempting to perform an action.
   */
  @inline private def attempt[F[_], A](f: => A)(implicit bracket: Bracket[F, Throwable], defer: Defer[F]): F[A] =
    defer.defer(bracket.catchNonFatal(f))

}
