package net.wayfarerx.teslore.model

/**
 * A single story.
 *
 * @param title      The title of this story.
 * @param author     The author of this story.
 * @param paragraphs The paragraphs in this story.
 */
case class Story(title: Title, author: Author, paragraphs: List[Paragraph])
