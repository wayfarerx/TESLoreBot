package net.wayfarerx.teslore
package materialize

/**
 * An interface into a storage system.
 *
 * @tparam F The type of effect used by this storage system.
 */
trait Storage[F[_]] {

  import Storage.Kind

  /**
   * Attempts to load the index HTML file from this storage system.
   *
   * @return The result of attempting to load the index HTML file from this storage system.
   */
  final def loadIndex: F[Option[library.Index]] = load(Kind.Index, Kind.Index.id)

  /**
   * Attempts to save the index HTML file to this storage system.
   *
   * @param index The index to save.
   * @return The result of attempting to save the index HTML file to this storage system.
   */
  final def saveIndex(index: library.Index): F[Unit] = save(Kind.Index, Kind.Index.id, index)

  /**
   * Attempts to delete the index HTML file from this storage system.
   *
   * @return The result of attempting to delete the index HTML file from this storage system.
   */
  final def deleteIndex(): F[Unit] = delete(Kind.Index, Kind.Index.id)

  /**
   * Attempts to return the names of the HTML files in this storage system.
   */
  final def htmlNames: F[List[String]] = list(Kind.Html)

  /**
   * Attempts to load the text of an HTML file from this storage system.
   *
   * @param name The name of the HTML file to load.
   * @return The result of attempting to load the text of an HTML file from this storage system.
   */
  final def loadHtml(name: String): F[Option[String]] = load(Kind.Html, name)

  /**
   * Attempts to save the text of an HTML file to this storage system.
   *
   * @param name The name of the HTML file to save.
   * @param html The text of the HTML file to save.
   * @return The result of attempting to save the text of an HTML file to this storage system.
   */
  final def saveHtml(name: String, html: String): F[Unit] = save(Kind.Html, name, html)

  /**
   * Attempts to delete an HTML file from this storage system.
   *
   * @param name The name of the HTML file to delete.
   * @return The result of attempting to delete an HTML file from this storage system.
   */
  final def deleteHtml(name: String): F[Unit] = delete(Kind.Html, name)

  /**
   * Attempts to return the names of the scrape files in this storage system.
   */
  final def scrapeNames: F[List[String]] = list(Kind.Scrape)

  /**
   * Attempts to load the content of a scrape file from this storage system.
   *
   * @param name The name of the scrape file to load.
   * @return The result of attempting to load the content of a scrape file from this storage system.
   */
  final def loadScrape(name: String): F[Option[library.Scrape]] = load(Kind.Scrape, name)

  /**
   * Attempts to save the content of a scrape file to this storage system.
   *
   * @param name   The name of the scrape file to save.
   * @param scrape The content of the scrape file to save.
   * @return The result of attempting to save the content of a scrape file to this storage system.
   */
  final def saveScrape(name: String, scrape: library.Scrape): F[Unit] = save(Kind.Scrape, name, scrape)

  /**
   * Attempts to delete a scrape file from this storage system.
   *
   * @param name The name of the scrape file to delete.
   * @return The result of attempting to delete a scrape file from this storage system.
   */
  final def deleteScrape(name: String): F[Unit] = delete(Kind.Scrape, name)

  /**
   * Attempts to return the names of the story files in this storage system.
   */
  final def storyNames: F[List[String]] = list(Kind.Story)

  /**
   * Attempts to load the content of a story file from this storage system.
   *
   * @param name The name of the story file to load.
   * @return The result of attempting to load the content of a story file from this storage system.
   */
  final def loadStory(name: String): F[Option[model.Story]] = load(Kind.Story, name)

  /**
   * Attempts to save the content of a story file to this storage system.
   *
   * @param name  The name of the story file to save.
   * @param story The content of the story file to save.
   * @return The result of attempting to save the content of a story file to this storage system.
   */
  final def saveStory(name: String, story: model.Story): F[Unit] = save(Kind.Story, name, story)

  /**
   * Attempts to delete a story file from this storage system.
   *
   * @param name The name of the story file to delete.
   * @return The result of attempting to delete a story file from this storage system.
   */
  final def deleteStory(name: String): F[Unit] = delete(Kind.Story, name)

  /**
   * Attempts to load the markov model file from this storage system.
   *
   * @return The result of attempting to load the markov model file from this storage system.
   */
  final def loadMarkov: F[Option[generator.Markov]] = load(Kind.Markov, Kind.Markov.id)

  /**
   * Attempts to save the markov model file to this storage system.
   *
   * @param markov The the markov model to save.
   * @return The result of attempting to save the markov model file to this storage system.
   */
  final def saveMarkov(markov: generator.Markov): F[Unit] = save(Kind.Markov, Kind.Markov.id, markov)

  /**
   * Attempts to delete the markov model file from this storage system.
   *
   * @return The result of attempting to delete the markov model file from this storage system.
   */
  final def deleteMarkov(): F[Unit] = delete(Kind.Markov, Kind.Markov.id)

  /**
   * Attempts to return the names of the specified files in this storage system.
   *
   * @param kind The kind of files to return the names of.
   */
  def list(kind: Kind[_]): F[List[String]]

  /**
   * Attempts to load the content of the specified file from this storage system.
   *
   * @tparam A The type of data to load.
   * @param kind The kind of file to load.
   * @param name The name of the file to load.
   * @return The result of attempting to load the content of the specified file from this storage system.
   */
  def load[A](kind: Kind[A], name: String): F[Option[A]]

  /**
   * Attempts to save the content of the specified file to this storage system.
   *
   * @tparam A The type of data to save.
   * @param kind    The kind of file to save.
   * @param name    The name of the file to save.
   * @param content The content of the file to save.
   * @return The result of attempting to save the content of the specified file to this storage system.
   */
  def save[A](kind: Kind[A], name: String, content: A): F[Unit]

  /**
   * Attempts to delete the specified file from this storage system.
   *
   * @param kind The kind of file to delete.
   * @param name The name of the file to delete.
   * @return The result of attempting to delete the specified file from this storage system.
   */
  def delete(kind: Kind[_], name: String): F[Unit]

}

/**
 * Definitions of the default storage system(s).
 */
object Storage {

  import java.nio.file.Path

  import cats.ApplicativeError
  import cats.effect.{Blocker, ContextShift, Sync}
  import cats.implicits._

  import io.circe.{Decoder, Encoder}
  import io.circe.generic.auto._
  import io.circe.parser._

  /**
   * Base class for kinds of data that can be stored.
   *
   * @tparam A The type of the data being stored.
   */
  sealed trait Kind[A] {

    /** The ID of this kind. */
    def id: String

    /** The extension used for this kind of stored data. */
    def extension: String

    /** The size of chunks to load from storage. */
    def chunkSize: Int

    /**
     * Encodes the specified decoded data.
     *
     * @tparam F The type of effect to use.
     * @param name    The name of the data being encoded.
     * @param decoded The data to encode.
     * @return The encoded data.
     */
    def encode[F[_] : Kind.Effect](name: String, decoded: A): F[String]

    /**
     * Decodes the specified encoded data.
     *
     * @tparam F The type of effect to use.
     * @param name    The name of the data being decoded.
     * @param encoded The data to decode.
     * @return The decoded data.
     */
    def decode[F[_] : Kind.Effect](name: String, encoded: String): F[A]

  }

  /**
   * Definitions of the supported storage data types.
   */
  object Kind {

    /** The alias for throwable applicative error support. */
    type Effect[F[_]] = ApplicativeError[F, Throwable]

    /**
     * Factory for implicit throwable applicative error support.
     */
    object Effect {

      /**
       * Returns the implicit throwable applicative error support.
       *
       * @tparam F The type to support.
       * @param result The implicit throwable applicative error support.
       * @return Returns the implicit throwable applicative error support.
       */
      def apply[F[_]](implicit result: Effect[F]): Effect[F] = result

    }

    /**
     * The index HTML storage data type.
     */
    case object Index extends Kind[library.Index] {

      /* Return the ID of this kind. */
      override def id: String = "index"

      /* Return the extension to use. */
      override def extension: String = "html"

      /* Return the chunk size to use. */
      override def chunkSize: Int = 2048

      /* Encode the specified data. */
      override def encode[F[_] : Effect](name: String, decoded: library.Index): F[String] =
        Effect[F].pure(decoded.html)

      /* Decode the specified string. */
      override def decode[F[_] : Effect](name: String, encoded: String): F[library.Index] =
        Effect[F].pure(library.Index(encoded))

    }

    /**
     * The HTML storage data type.
     */
    case object Html extends Kind[String] {

      /* Return the ID of this kind. */
      override def id: String = "html"

      /* Return the extension to use. */
      override def extension: String = "html"

      /* Return the chunk size to use. */
      override def chunkSize: Int = 2048

      /* Encode the specified data. */
      override def encode[F[_] : Effect](name: String, decoded: String): F[String] =
        Effect[F].pure(decoded)

      /* Decode the specified string. */
      override def decode[F[_] : Effect](name: String, encoded: String): F[String] =
        Effect[F].pure(encoded)

    }

    /**
     * The scrape storage data type.
     */
    case object Scrape extends Kind[library.Scrape] {

      /* Return the ID of this kind. */
      override def id: String = "scrape"

      /* Return the extension to use. */
      override def extension: String = "txt"

      /* Return the chunk size to use. */
      override def chunkSize: Int = 1024

      /* Encode the specified data. */
      override def encode[F[_] : Effect](name: String, decoded: library.Scrape): F[String] =
        Effect[F].pure(decoded.title :: decoded.author :: decoded.paragraphs mkString "\r\n")

      /* Decode the specified string. */
      override def decode[F[_] : Effect](name: String, encoded: String): F[library.Scrape] =
        encoded.split("""[\r\n]+""").toList match {
          case title :: author :: content => Effect[F].pure(library.Scrape(title, author, content))
          case _ => Effect[F].raiseError(new IllegalArgumentException(s"""Failed to decode scrape "$name""""))
        }

    }

    sealed abstract class Json[A: Encoder : Decoder] extends Kind[A] {

      /* Return the extension to use. */
      final override def extension: String = "json"

      /* Return the chunk size to use. */
      final override def chunkSize: Int = 2048

      /* Encode the specified data. */
      final override def encode[F[_] : Effect](name: String, decoded: A): F[String] =
        Effect[F].pure(Encoder[A].apply(decoded).spaces2)

      /* Decode the specified string. */
      final override def decode[F[_] : Effect](name: String, encoded: String): F[A] =
        parse(encoded).leftMap { failure =>
          new IllegalStateException(s"""Failed to parse $id "$name": ${failure.getMessage}""")
        }.flatMap { json =>
          Decoder[A].decodeJson(json) leftMap { failure =>
            new IllegalArgumentException(s"""Failed to decode $id "$name": ${failure.getMessage}""")
          }
        }.fold(Effect[F].raiseError, Effect[F].pure)

    }

    /**
     * The story storage data type.
     */
    case object Story extends Json[model.Story] {

      /* The ID of this kind. */
      override val id: String = "story"

    }

    /**
     * The markov storage data type.
     */
    case object Markov extends Json[generator.Markov] {

      /* The ID of this kind. */
      override val id: String = "markov"

    }

  }

  /**
   * A storage system that uses the file system.
   *
   * @tparam F The type of effect used by this storage system.
   * @param blocker         The blocking execution context to use.
   * @param indexDirectory  The directory containing the index file.
   * @param htmlDirectory   The directory containing the HTML files.
   * @param scrapeDirectory The directory containing the scrape files.
   * @param storyDirectory  The directory containing the story files.
   * @param markovDirectory The directory containing the markov file.
   */
  case class FileSystem[F[_] : ContextShift : Sync](
    blocker: Blocker,
    indexDirectory: Path,
    htmlDirectory: Path,
    scrapeDirectory: Path,
    storyDirectory: Path,
    markovDirectory: Path
  ) extends Storage[F] {

    /**
     * Returns the path to the directory where the specified kind of file is stored.
     *
     * @param kind The kind of file in question.
     * @return The path to the directory where the specified kind of file is stored.
     */
    private def directory(kind: Kind[_]): Path = kind match {
      case Kind.Index => indexDirectory
      case Kind.Html => htmlDirectory
      case Kind.Scrape => scrapeDirectory
      case Kind.Story => storyDirectory
      case Kind.Markov => markovDirectory
    }

    /**
     * Attempts to return the path to a file with the specified kind and name.
     *
     * @param kind The kind of the file in question.
     * @param name The name of the file in question.
     * @return The result of attempting to return the path to a file with the specified kind and name.
     */
    private def file(kind: Kind[_], name: String): F[Path] =
      blocker.blockOn(Sync[F].defer(Sync[F].catchNonFatal(directory(kind).resolve(s"$name.${kind.extension}"))))

    /* Attempt to return the names of the specified files in this storage system. */
    override def list(kind: Kind[_]): F[List[String]] = {
      val suffix = s".${kind.extension}"
      fs2.io.file.directoryStream(blocker, directory(kind)).map(_.getFileName.toString).collect {
        case name if name.length > suffix.length && name.endsWith(suffix) =>
          name.substring(0, name.length - suffix.length)
      }.compile.toList
    }

    /* Attempt to load the content of the specified file from this storage system. */
    override def load[A](kind: Kind[A], name: String): F[Option[A]] = for {
      target <- file(kind, name)
      exists <- fs2.io.file.exists(blocker, target)
      result <- if (!exists) Sync[F].pure(Option.empty[A]) else
        fs2.io.file.readAll(target, blocker, kind.chunkSize)
          .through(fs2.text.utf8Decode).compile.foldMonoid
          .flatMap(kind.decode[F](name, _))
          .map(Option(_))
    } yield result

    /* Attempt to save the content of the specified file to this storage system. */
    override def save[A](kind: Kind[A], name: String, content: A): F[Unit] = for {
      target <- file(kind, name)
      encoded <- kind.encode[F](name, content)
      _ <- fs2.Stream(encoded).through(fs2.text.utf8Encode).through(fs2.io.file.writeAll(target, blocker)).compile.drain
    } yield ()

    /* Attempt to delete the specified file from this storage system. */
    override def delete(kind: Kind[_], name: String): F[Unit] = for {
      target <- file(kind, name)
      _ <- fs2.io.file.delete(blocker, target)
    } yield ()

  }

  /**
   * Factory for file system based storage systems.
   */
  object FileSystem {

    import java.nio.file.Paths

    import concurrent.ExecutionContext

    /**
     * Attempts to create a file system with the current location.
     *
     * @tparam F The type of effect used by the file system.
     * @param blockingContext The blocking execution context to use.
     * @return The result of attempting to create a file system with the current location.
     */
    def apply[F[_] : ContextShift : Sync]()(implicit blockingContext: ExecutionContext): F[FileSystem[F]] =
      FileSystem(Blocker.liftExecutionContext(blockingContext))

    /**
     * Attempts to create a file system with the current location.
     *
     * @tparam F The type of effect used by the file system.
     * @param blocker The blocking execution context to use.
     * @return The result of attempting to create a file system with the current location.
     */
    def apply[F[_] : ContextShift : Sync](blocker: Blocker): F[FileSystem[F]] =
      FileSystem(blocker, ".")

    /**
     * Attempts to create a file system with the specified location.
     *
     * @tparam F The type of effect used by the file system.
     * @param root            The location of the root directory to use.
     * @param blockingContext The blocking execution context to use.
     * @return The result of attempting to create a file system with the current location.
     */
    def apply[F[_] : ContextShift : Sync](root: String)(implicit blockingContext: ExecutionContext): F[FileSystem[F]] =
      FileSystem(Blocker.liftExecutionContext(blockingContext), root)

    /**
     * Attempts to create a file system with the specified root directory location.
     *
     * @tparam F The type of effect used by the file system.
     * @param blocker The blocking execution context to use.
     * @param root    The location of the root directory to use.
     * @return The result of attempting to create a file system with the specified root directory location.
     */
    def apply[F[_] : ContextShift : Sync](blocker: Blocker, root: String): F[FileSystem[F]] = for {
      resolved <- resolveDirectory(blocker, root)
      fileSystem <- FileSystem(blocker, resolved)
    } yield fileSystem

    /**
     * Attempts to create a file system with the specified location.
     *
     * @tparam F The type of effect used by the file system.
     * @param root            The root directory to use.
     * @param blockingContext The blocking execution context to use.
     * @return The result of attempting to create a file system with the current location.
     */
    def apply[F[_] : ContextShift : Sync](root: Path)(implicit blockingContext: ExecutionContext): F[FileSystem[F]] =
      FileSystem(Blocker.liftExecutionContext(blockingContext), root)

    /**
     * Attempts to create a file system with the specified root directory.
     *
     * @tparam F The type of effect used by the file system.
     * @param blocker The blocking execution context to use.
     * @param root    The root directory to use.
     * @return The result of attempting to create a file system with the specified root directory.
     */
    def apply[F[_] : ContextShift : Sync](blocker: Blocker, root: Path): F[FileSystem[F]] = for {
      indexDirectory <- resolveDirectory(blocker, root, Kind.Index.id)
      htmlDirectory <- resolveDirectory(blocker, root, Kind.Html.id)
      scrapeDirectory <- resolveDirectory(blocker, root, Kind.Scrape.id)
      storyDirectory <- resolveDirectory(blocker, root, Kind.Story.id)
      markovDirectory <- resolveDirectory(blocker, root, Kind.Markov.id)
    } yield FileSystem(blocker, indexDirectory, htmlDirectory, scrapeDirectory, storyDirectory, markovDirectory)

    /**
     * Attempts to create a file system with the specified storage directory locations.
     *
     * @tparam F The type of effect used by the file system.
     * @param htmlDirectory   The location of the directory containing the HTML files.
     * @param scrapeDirectory The location of the directory containing the scrape files.
     * @param storyDirectory  The location of the directory containing the story files.
     * @param markovDirectory The location of the directory containing the markov file.
     * @param blockingContext The blocking execution context to use.
     * @return The result of attempting to create a file system with the specified storage directory locations.
     */
    def apply[F[_] : ContextShift : Sync](
      indexDirectory: String,
      htmlDirectory: String,
      scrapeDirectory: String,
      storyDirectory: String,
      markovDirectory: String
    )(implicit blockingContext: ExecutionContext): F[FileSystem[F]] =
      FileSystem(
        Blocker.liftExecutionContext(blockingContext),
        indexDirectory,
        htmlDirectory,
        scrapeDirectory,
        storyDirectory,
        markovDirectory
      )

    /**
     * Attempts to create a file system with the specified storage directory locations.
     *
     * @tparam F The type of effect used by the file system.
     * @param blocker         The blocking execution context to use.
     * @param indexDirectory  The location of the directory containing the index file.
     * @param htmlDirectory   The location of the directory containing the HTML files.
     * @param scrapeDirectory The location of the directory containing the scrape files.
     * @param storyDirectory  The location of the directory containing the story files.
     * @param markovDirectory The location of the directory containing the markov file.
     * @return The result of attempting to create a file system with the specified storage directory locations.
     */
    def apply[F[_] : ContextShift : Sync](
      blocker: Blocker,
      indexDirectory: String,
      htmlDirectory: String,
      scrapeDirectory: String,
      storyDirectory: String,
      markovDirectory: String
    ): F[FileSystem[F]] = for {
      _indexDirectory <- resolveDirectory(blocker, indexDirectory)
      _htmlDirectory <- resolveDirectory(blocker, htmlDirectory)
      _scrapeDirectory <- resolveDirectory(blocker, scrapeDirectory)
      _storyDirectory <- resolveDirectory(blocker, storyDirectory)
      _markovDirectory <- resolveDirectory(blocker, markovDirectory)
    } yield FileSystem(blocker, _indexDirectory, _htmlDirectory, _scrapeDirectory, _storyDirectory, _markovDirectory)

    /**
     * Returns a file system with the specified storage directory locations.
     *
     * @tparam F The type of effect used by the file system.
     * @param indexDirectory  The directory containing the index file.
     * @param htmlDirectory   The directory containing the HTML files.
     * @param scrapeDirectory The directory containing the scrape files.
     * @param storyDirectory  The directory containing the story files.
     * @param markovDirectory The directory containing the markov file.
     * @param blockingContext The blocking execution context to use.
     * @return A file system with the specified storage directory locations.
     */
    def apply[F[_] : ContextShift : Sync](
      indexDirectory: Path,
      htmlDirectory: Path,
      scrapeDirectory: Path,
      storyDirectory: Path,
      markovDirectory: Path
    )(implicit blockingContext: ExecutionContext): FileSystem[F] =
      FileSystem(
        Blocker.liftExecutionContext(blockingContext),
        indexDirectory,
        htmlDirectory,
        scrapeDirectory,
        storyDirectory,
        markovDirectory
      )

    /**
     * Attempts to resolve a path.
     *
     * @tparam F The type of effect to use.
     * @param blocker         The blocking execution context to use.
     * @param location        The location of the path to resolve.
     * @return The result of attempting to resolve a path.
     */
    private def resolveDirectory[F[_] : ContextShift : Sync](blocker: Blocker, location: String): F[Path] = for {
      path <- blocker.blockOn(Sync[F].defer(Sync[F].catchNonFatal(Paths.get(location))))
      _ <- createDirectory(blocker, path)
    } yield path

    /**
     * Attempts to resolve a path from a parent and child.
     *
     * @tparam F The type of effect to use.
     * @param blocker The blocking execution context to use.
     * @param parent  The parent path to resolve against.
     * @param child   The location of the child path to resolve.
     * @return The result of attempting to resolve a path from a parent and child.
     */
    private def resolveDirectory[F[_] : ContextShift : Sync](blocker: Blocker, parent: Path, child: String): F[Path] =
      for {
      path <- blocker.blockOn(Sync[F].defer(Sync[F].catchNonFatal(parent.resolve(child))))
      _ <- createDirectory(blocker, path)
    } yield path

    /**
     * Attempts to ensure the specified directory exists.
     *
     * @tparam F The type of effect to use.
     * @param blocker The blocking execution context to use.
     * @param path The path of the directory that should exist.
     * @return The result of attempting to ensure the specified directory exists.
     */
    private def createDirectory[F[_] : ContextShift : Sync](blocker: Blocker, path: Path): F[Unit] = for {
      exists <- fs2.io.file.exists(blocker, path)
      _ <- if (exists) Sync[F].unit else fs2.io.file.createDirectories(blocker, path) map (_ => ())
    } yield ()

  }

}
