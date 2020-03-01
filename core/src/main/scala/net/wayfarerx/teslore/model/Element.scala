package net.wayfarerx.teslore.model

/**
 * An element of a sentence.
 */
sealed trait Element

/**
 * Factory for elements.
 */
object Element {

  /**
   * Any element that is a name.
   *
   * @param items The items that comprise this name element.
   */
  case class Name(items: List[String]) extends Element

  /**
   * Any element that is not a name.
   *
   * @param text The text of this element.
   * @param pos  The part of speech of this element.
   */
  case class Word(text: String, pos: String) extends Element

}