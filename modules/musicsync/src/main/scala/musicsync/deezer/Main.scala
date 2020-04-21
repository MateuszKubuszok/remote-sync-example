package musicsync.deezer

import cats.effect.ExitCode
import com.softwaremill.sttp.okhttp.monix.OkHttpMonixBackend
import com.typesafe.scalalogging.Logger
import monix.eval.{ Task, TaskApp }
import musicsync.deezer.integration.client.DeezerClient
import musicsync.deezer.integration.model.AccessToken
import pureconfig._
import pureconfig.generic.auto._

object Main extends TaskApp {

  val logger = Logger(getClass)

  final case class Config(accessToken: AccessToken)

  override def run(args: List[String]): Task[ExitCode] =
    for {
      Config(accessToken) <- Task(ConfigSource.default.loadOrThrow[Config])
      client = new DeezerClient(OkHttpMonixBackend())
      albums <- client.user.albums(accessToken -> None)
      _ <- Task(logger.info(s"Album:\n$albums"))
      artists <- client.user.artists(accessToken -> None)
      _ <- Task(logger.info(s"Artists:\n$artists"))
      tracks <- client.user.tracks(accessToken -> None)
      _ <- Task(logger.info(s"Tracks:\n$tracks"))
    } yield ExitCode.Success
}
