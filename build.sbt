import Dependencies._

lazy val common = Seq(
  ThisBuild / scalaVersion := "2.13.1",
  ThisBuild / organization := "net.wayfarerx",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  addCompilerPlugin(KindProjectorPlugin)
)

lazy val TESLoreBot = (project in file("."))
  .settings(skip in publish := true)
  .aggregate(core, materialize)

lazy val core = (project in file("core")).settings(
  common,
  ThisBuild / name := "tes-lore-bot",
  libraryDependencies ++= Seq(
    CirceCore,
    CirceGeneric,
    CirceParser,
    CatsEffect,
    OpenNlpTools,
    JSoup,
    ScalaTest % Test
  )
)

lazy val materialize = (project in file("materialize")).settings(
  common,
  ThisBuild / name := "tes-lore-bot-materialize",
  libraryDependencies ++= Seq(
    Fs2Core,
    Fs2Io
  )
).dependsOn(core)