import sbt._

object Dependencies {

  lazy val KindProjectorVersion = "0.11.0"
  lazy val KindProjectorPlugin = "org.typelevel" %% "kind-projector" % KindProjectorVersion cross CrossVersion.full

  lazy val CirceVersion = "0.12.3"
  lazy val CirceCore = "io.circe" %% "circe-core" % CirceVersion
  lazy val CirceGeneric = "io.circe" %% "circe-generic" % CirceVersion
  lazy val CirceParser = "io.circe" %% "circe-parser" % CirceVersion

  lazy val CatsEffectVersion = "2.1.1"
  lazy val CatsEffect = "org.typelevel" %% "cats-effect" % CatsEffectVersion

  lazy val Fs2Version = "2.2.1"
  lazy val Fs2Core = "co.fs2" %% "fs2-core" % Fs2Version
  lazy val Fs2Io = "co.fs2" %% "fs2-io" % Fs2Version

  lazy val OdinVersion = "0.7.0"
  lazy val OdinCore = "com.github.valskalla" %% "odin-core" % OdinVersion

  lazy val JSoupVersion = "1.12.1"
  lazy val JSoup = "org.jsoup" % "jsoup" % JSoupVersion

  lazy val OpenNlpVersion = "1.9.2"
  lazy val OpenNlpTools = "org.apache.opennlp" % "opennlp-tools" % OpenNlpVersion

  lazy val ScalaTestVersion = "3.1.0"
  lazy val ScalaTest = "org.scalatest" %% "scalatest" % ScalaTestVersion

}
