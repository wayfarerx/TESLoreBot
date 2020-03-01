package net.wayfarerx.teslore.library

/**
 * The content scraped from an HTML file.
 *
 * @param title      The title of this scrape.
 * @param author     The author of this scrape.
 * @param paragraphs The paragraphs in this scrape.
 */
case class Scrape(title: String, author: String, paragraphs: List[String])

/**
 * Factory for HTML content scrapes.
 */
object Scrape extends ((String, String, List[String]) => Scrape) {

  import scala.jdk.CollectionConverters._

  import org.jsoup.Jsoup

  /** The lower-case string at the front of author declarations. */
  private val AuthorPrefix = "author:"

  /**
   * Attempts to scrape content from the specified html.
   *
   * @param html The HTML to scape for content.
   * @return The result of attempting to scrape content from the specified html.
   */
  def apply(html: String): Option[Scrape] = {
    val doc = Jsoup.parse(html)
    val title = doc.select("h1.page-title").iterator.asScala.map(_.text).mkString(" ").trim
    val author = doc.select("div.field-field-author").iterator.asScala.map(_.text).mkString(" ").trim match {
      case author if author.toLowerCase startsWith AuthorPrefix => author.drop(AuthorPrefix.length).trim
      case author => author
    }
    if (title.isEmpty || author.isEmpty) None else Some(Scrape(title, author,
      doc.select(".node-book .prose p").iterator.asScala.map(_.text).flatMap { paragraph =>
        Some(paragraph.replaceAll("""\s+""", " ").trim) filterNot (_.isEmpty)
      }.toList
    ))
  }

}
