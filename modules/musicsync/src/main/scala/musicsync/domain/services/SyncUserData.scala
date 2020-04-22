package musicsync.domain.services

import cats.data.Chain
import cats.implicits._
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import musicsync.deezer.integration.model.DeezerArtist
import musicsync.deezer.services.DeezerStreams
import musicsync.domain.model.{ Artist, ID, User }
import musicsync.persistence.model.ArtistDTO
import musicsync.persistence.repositories.ArtistDTORepository

trait SyncUserData {

  def syncArtists(user: User): Task[Unit]
}

class SyncDeezerUserData(
  accessTokenService:  AccessTokenService,
  deezerStreams:       DeezerStreams,
  artistDTORepository: ArtistDTORepository
) extends SyncUserData {

  private val logger = Logger(getClass)

  def syncArtists(user: User): Task[Unit] =
    accessTokenService.deezerAccessToken(user.id).flatMap {
      case Some(accessToken) =>
        deezerStreams.usersArtists(accessToken).evalMap(createArtistIfNotExist).compile.foldMonoid.flatMap {
          artistIDs: Chain[ID[Artist]] =>
            // list of artists for user is set in repository to calculated value
            artistDTORepository.setForUser(user.id.value, artistIDs.map(_.value).toList.toSet)
        }

      case None =>
        // access token not found in DB, skipping
        Task {
          logger.info(s"Access Token for $user not found, skipping")
        }
    }

  private def createArtistIfNotExist(deezerArtist: DeezerArtist): Task[Chain[ID[Artist]]] =
    artistDTORepository.findByDeezerID(deezerArtist.id).flatMap {
      case Some(artist) =>
        // artist is in the database already
        Task.pure(ID.parse[Artist](artist.id) match {
          case Right(id) => Chain.one(id)
          case Left(_)   => Chain.empty[ID[Artist]]
        })

      case None =>
        // artist has to be added to the database
        deezerArtist.toDomainNew match {
          case Left(error) =>
            // error happened during parsing
            Task {
              logger.warn(s"Cannot create a new Artist:\n  $deezerArtist\n  validation failed: $error")
              Chain.empty[ID[Artist]]
            }

          case Right(artist) =>
            // artist can be created
            artistDTORepository.create(ArtistDTO.fromDomain(artist)).map(_ => Chain.one(artist.id))
        }
    }
}
