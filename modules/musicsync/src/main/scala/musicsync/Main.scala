package musicsync

import java.util.UUID

import cats.effect.ExitCode
import com.softwaremill.sttp.okhttp.monix.OkHttpMonixBackend
import com.typesafe.scalalogging.Logger
import monix.eval.{ Task, TaskApp }
import musicsync.deezer.integration.client.DeezerClient
import musicsync.deezer.integration.model.AccessToken
import musicsync.deezer.services.DeezerStreams
import musicsync.domain.model.{ ID, User }
import musicsync.domain.services.{ MockAccessTokenService, SyncDeezerUserData }
import musicsync.persistence.repositories._
import pureconfig._
import pureconfig.generic.auto._

object Main extends TaskApp {

  val logger = Logger(getClass)

  final case class Config(accessToken: AccessToken, database: Database.Config)

  override def run(args: List[String]): Task[ExitCode] =
    if (args.contains("in-memory")) inMemoryRun else postgresRun

  val mockUser = User(ID[User](UUID.fromString("c380d6cb-b66d-40ac-a2a0-fd11c53be7db")))

  def postgresRun: Task[ExitCode] =
    Task(ConfigSource.default.loadOrThrow[Config]).flatMap {
      case Config(accessToken, dbCfg) =>
        val database = Database(dbCfg)
        val client   = new DeezerClient(OkHttpMonixBackend())
        val streams  = new DeezerStreams(client)

        database.makeTransactor.use { transactor =>
          val artistDTORepository = new PostgresArtistDTORepository(transactor)
          val accessTokenService  = new MockAccessTokenService(accessToken)

          for {
            _ <- Task(logger.info(s"Run migration before routine"))
            _ <- database.migrate
            _ <- Task(logger.info(s"Synchronization started"))
            syncUserData = new SyncDeezerUserData(accessTokenService, streams, artistDTORepository)
            _ <- syncUserData.syncArtists(mockUser)
            _ <- Task(logger.info(s"Synchronization finished"))
            _ <- Task(logger.info(s"Done running streams"))
          } yield ExitCode.Success
        }
    }

  def inMemoryRun: Task[ExitCode] =
    for {
      Config(accessToken, _) <- Task(ConfigSource.default.loadOrThrow[Config])
      database           = Database
      client             = new DeezerClient(OkHttpMonixBackend())
      streams            = new DeezerStreams(client)
      accessTokenService = new MockAccessTokenService(accessToken)
      artistDTORepository <- ArtistDTORepositoryInMemory.create()
      _ <- Task(logger.info(s"Synchronization started"))
      syncUserData = new SyncDeezerUserData(accessTokenService, streams, artistDTORepository)
      _ <- syncUserData.syncArtists(mockUser)
      _ <- Task(logger.info(s"Synchronization finished"))
      _ <- Task(logger.info(s"Preview artist in DB"))
      _ <- artistDTORepository.artists.get.map(_.toString).map(logger.info(_))
      _ <- Task(logger.info(s"Preview artist-user connections in DB"))
      _ <- artistDTORepository.userArtists.get.map(_.toString).map(logger.info(_))
      _ <- Task(logger.info(s"Done running streams"))
    } yield ExitCode.Success
}
