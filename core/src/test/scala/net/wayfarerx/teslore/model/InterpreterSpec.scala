package net.wayfarerx.teslore.model

//import model._
//
//import cats.effect.IO

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Test suite for the Imperial Library Interpreter.
 */
class InterpreterSpec extends AnyFlatSpec with Matchers {

//  "The Imperial Library Interpreter" should "interpret a basic story" in {
//    val program = for {
//      interpreter <- Interpreter[IO]()
//      story <- interpreter.interpretStory("Story", "wayfarerx", "This is a story.")
//    } yield story
//    program.unsafeRunSync() shouldBe Story(
//      Title(List(Element.Name(List("Story")))),
//      Author(Element.Name(List("wayfarerx"))),
//      List(Paragraph(List(Sentence(List("This", "is", "a", "story", ".") map Element.Word)))))
//  }
//
//  ignore should "interpret a basic story with names" in {
//    val program = for {
//      interpreter <- Interpreter[IO]()
//      story <- interpreter.interpretStory("Story", "wayfarerx",
//        "About Gideon appearing both as a city in Cyrodiil and as a city in Black Marsh, and about " +
//          "Wroth Naga apparently overlooking the Wrothgarian Mountains."
//      )
//    } yield story
//    program.unsafeRunSync() shouldBe Story(
//      Title(List(Element.Name(List("Story")))),
//      Author(Element.Name(List("wayfarerx"))),
//      List(Paragraph(List(Sentence(
//        Element.Other("About") :: Element.Name(List("Gideon")) ::
//          List("appearing", "both", "as", "a", "city", "in").map(Element.Word) :::
//          List(Element.Name(List("Cyrodiil"))) :::
//          List("and", "as", "a", "city", "in").map(Element.Word) :::
//          List(Element.Name(List("Black", "Marsh"))) :::
//          List(",", "and", "about").map(Element.Word) :::
//          List(Element.Name(List("Wroth", "Naga"))) :::
//          List("apparently", "overlooking", "the").map(Element.Word) :::
//          List(Element.Name(List("Wrothgarian", "Mountains"))) :::
//          List(Element.Other("."))
//      ))))
//    )
//  }
//
//  it should "interpret a complex story" in {
//    val program = for {
//      interpreter <- Interpreter[IO]()
//      story <- interpreter.interpretStory("Story", "wayfarerx",
//        "This is paragraph one.",
//        "This is paragraph two. It has two sentences."
//      )
//    } yield story
//    program.unsafeRunSync() shouldBe Story(
//      Title(List(Element.Name(List("Story")))),
//      Author(Element.Name(List("wayfarerx"))),
//      List(
//        Paragraph(List(Sentence(List("This", "is", "paragraph", "one", ".") map Element.Word))),
//        Paragraph(List(
//          Sentence(List("This", "is", "paragraph", "two", ".") map Element.Word),
//          Sentence(List("It", "has", "two", "sentences", ".") map Element.Word)
//        ))
//      ))
//  }

}
