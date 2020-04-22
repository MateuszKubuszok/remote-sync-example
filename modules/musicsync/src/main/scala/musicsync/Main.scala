package musicsync

import cats.effect.ExitCode
import com.softwaremill.sttp.okhttp.monix.OkHttpMonixBackend
import com.typesafe.scalalogging.Logger
import monix.eval.{ Task, TaskApp }
import musicsync.deezer.integration.client.DeezerClient
import musicsync.deezer.integration.model.AccessToken
import musicsync.deezer.services.DeezerStreams
import pureconfig._
import pureconfig.generic.auto._

object Main extends TaskApp {

  val logger = Logger(getClass)

  final case class Config(accessToken: AccessToken)

  override def run(args: List[String]): Task[ExitCode] =
    for {
      Config(accessToken) <- Task(ConfigSource.default.loadOrThrow[Config])
      client  = new DeezerClient(OkHttpMonixBackend())
      streams = new DeezerStreams(client)
      _ <- streams.usersAlbums(accessToken).evalTap(album => Task(logger.info(s"Album:\n$album\n"))).compile.drain
      _ <- streams.usersArtists(accessToken).evalTap(album => Task(logger.info(s"Artist:\n$album\n"))).compile.drain
      _ <- streams.usersTracks(accessToken).evalTap(album => Task(logger.info(s"Track:\n$album\n"))).compile.drain
      _ <- Task(logger.info(s"Done running streams"))
    } yield ExitCode.Success
}
