package net.wayfarerx.teslore.library

import scala.jdk.CollectionConverters._

import org.jsoup.Jsoup

/**
 * The index of the documents the TES lore bot tracks.
 *
 * @param html The HTML content of the index page.
 */
case class Index(html: String) {

  /** The URLs of the documents the TES lore bot tracks. */
  lazy val urls: List[String] =
    Jsoup.parse(html, ImperialLibraryUrl).select("ul .views-row").iterator.asScala.map {
      _ selectFirst "li span .field-content a" attr "abs:href"
    }.toList

}
