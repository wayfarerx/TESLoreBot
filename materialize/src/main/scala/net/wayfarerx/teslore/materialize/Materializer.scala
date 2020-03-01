package net.wayfarerx.teslore
package materialize

import java.net.URL

import concurrent.duration._

import cats.effect.{Blocker, ContextShift, Sync, Timer}
import cats.implicits._

/**
 * A utility that materializes the markov model.
 *
 * @tparam F The type of effect to use.
 * @param blocker     The blocking execution context to use.
 * @param storage     The storage system to use.
 * @param interpreter The language interpreter to use.
 */
case class Materializer[F[_] : ContextShift : Sync : Timer](
  blocker: Blocker,
  storage: Storage[F],
  interpreter: model.Interpreter[F]
) {

  import Materializer._

  /**
   * Attempts to materialize a markov model.
   *
   * @return The result of attempting to materialize a markov model.
   */
  def materialize: F[generator.Markov] = for {
    stored <- storage.loadMarkov
    markov <- stored map Sync[F].pure getOrElse {
      for {
        htmlNames <- storage.htmlNames map (_.toSet)
        scrapeNames <- storage.scrapeNames map (_.toSet)
        storyNames <- storage.storyNames map (_.toSet)
        index <- materializeIndex
        result <- materializeStories(htmlNames ++ scrapeNames ++ storyNames, index) map (generator.Markov(_))
        _ <- storage.saveMarkov(result)
      } yield result
    }
  } yield markov

  /**
   * Attempts to materialize the list of indexed URLs.
   *
   * @return The result of attempting to materialize the list of indexed URLs.
   */
  private def materializeIndex: F[library.Index] = for {
    stored <- storage.loadIndex
    index <- stored map Sync[F].pure getOrElse {
      for {
        result <- download(library.ImperialLibraryUrl, delay = false) map library.Index
        _ <- storage.saveIndex(result)
      } yield result
    }
  } yield index

  /**
   * Attempts to materialize all the known and specified stories.
   *
   * @param known The set of all known story names.
   * @param index The index of specified stories to load.
   * @return The result of attempting to materialize all the known and specified stories.
   */
  private def materializeStories(known: Set[String], index: library.Index): F[List[model.Story]] = {
    val entries = {
      val specified = index.urls.iterator.flatMap(head => extractName(head) map (_ -> Option(head))).toMap
      specified ++ (known -- specified.keys).iterator.map(_ -> Option.empty[String]).toMap
    }
    entries.foldRight(Sync[F].pure(List.empty[model.Story])) { (head, tail) =>
      for (h <- materializeStory(head._1, head._2); t <- tail) yield h map (_ :: t) getOrElse t
    }
  }

  /**
   * Extracts a name from the specified URL.
   *
   * @param url The URL to extract a name from.
   * @return The name extracted from the specified URL.
   */
  private def extractName(url: String): Option[String] = {

    @annotation.tailrec def trim(str: String): String =
      if (str endsWith "/") trim(str dropRight 1) else str

    val trimmed = trim(url)
    val result = trimmed lastIndexOf '/' match {
      case index if index >= 0 => trimmed.drop(index + 1)
      case _ => trimmed
    }
    if (result.isEmpty) None else Some(result)
  }

  /**
   * Attempts to materialize a story.
   *
   * @param name The name of the story to materialize.
   * @param url  The optional URL of the story's HTML.
   * @return The result of attempting to materialize a story.
   */
  private def materializeStory(name: String, url: Option[String]): F[Option[model.Story]] = for {
    stored <- storage.loadStory(name)
    story <- stored map (Sync[F] pure Option(_)) getOrElse {
      for {
        scrape <- materializeScrape(name, url)
        result <- scrape map (interpreter.interpret(_) map (Option(_))) getOrElse
          Sync[F].pure(Option.empty[model.Story])
        _ <- result map (storage.saveStory(name, _)) getOrElse Sync[F].unit
      } yield result
    }
  } yield story

  /**
   * Attempts to materialize a scrape.
   *
   * @param name The name of the scrape to materialize.
   * @param url  The optional URL of the scrape's HTML.
   * @return The result of attempting to materialize a scrape.
   */
  private def materializeScrape(name: String, url: Option[String]): F[Option[library.Scrape]] = for {
    stored <- storage.loadScrape(name)
    scrape <- stored map (Sync[F] pure Option(_)) getOrElse {
      for {
        html <- materializeHtml(name, url)
        result <- html flatMap (library.Scrape(_)) map (Sync[F] pure Option(_)) getOrElse {
          Sync[F].pure(Option.empty[library.Scrape])
        }
        _ <- result map (storage.saveScrape(name, _)) getOrElse Sync[F].unit
      } yield result
    }
  } yield scrape

  /**
   * Attempts to materialize an HTML file.
   *
   * @param name The name of the HTML file to materialize.
   * @param url  The optional URL of the HTML.
   * @return The result of attempting to materialize an HTML file.
   */
  private def materializeHtml(name: String, url: Option[String]): F[Option[String]] = for {
    stored <- storage.loadHtml(name)
    html <- stored map (Sync[F] pure Option(_)) getOrElse {
      for {
        result <- url map (download(_) map (Option(_))) getOrElse Sync[F].pure(Option.empty[String])
        _ <- result map (storage.saveHtml(name, _)) getOrElse Sync[F].unit
      } yield result
    }
  } yield html

  /**
   * Attempts to download UTF-8 text from a URL.
   *
   * @param url   The URL to download.
   * @param delay True if the download process should be delayed, defaults to true.
   * @return The result of attempting to download UTF-8 text from a URL.
   */
  private def download(url: String, delay: Boolean = true): F[String] = for {
    _ <- if (delay) Timer[F].sleep(Downtime) else Sync[F].unit
    _url <- blocker.blockOn(Sync[F].defer(Sync[F].catchNonFatal(new URL(url))))
    text <- fs2.io.readInputStream(
      blocker.blockOn(Sync[F].defer(Sync[F].catchNonFatal(_url.openStream()))),
      1024,
      blocker
    ).through(fs2.text.utf8Decode).compile.foldMonoid
  } yield text

}

/**
 * Definitions associated with the materializer.
 */
object Materializer {

  /** The downtime that is enforced between downloads. */
  val Downtime: FiniteDuration = 2.minutes

}