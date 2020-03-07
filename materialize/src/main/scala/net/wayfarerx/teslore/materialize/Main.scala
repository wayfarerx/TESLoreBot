package net.wayfarerx.teslore
package materialize

import java.util.concurrent.Executors

import concurrent.{ExecutionContext, ExecutionContextExecutorService}

import cats.effect.{ExitCode, IO, IOApp, Resource}

import io.odin._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Resource.make(acquireContext)(releaseContext) use { implicit blockingExecutionContext: ExecutionContext =>
      for {
        storage <- Storage.FileSystem[IO](args.head)
        interpreter <- model.Interpreter[IO]
        _ <- Materializer(consoleLogger[IO](), storage, interpreter).materialize
      } yield ExitCode.Success
    }

  private def acquireContext: IO[ExecutionContextExecutorService] =
    IO(ExecutionContext.fromExecutorService(Executors.newCachedThreadPool()))

  private def releaseContext(ctx: ExecutionContextExecutorService): IO[Unit] =
    IO(ctx.shutdownNow())

}
