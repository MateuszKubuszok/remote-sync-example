package musicsync.persistence.repositories

import java.util.UUID

import cats.implicits._
import cats.effect.concurrent.Ref
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import monix.eval.Task
import musicsync.persistence.model.ArtistDTO

trait ArtistDTORepository {

  def create(artistDTO: ArtistDTO): Task[Unit]

  def findByID(id: UUID): Task[Option[ArtistDTO]]

  def findByDeezerID(deezerID: Int): Task[Option[ArtistDTO]]

  def setForUser(userID: UUID, artistsIDs: Set[UUID]): Task[Unit]
}

class ArtistDTORepositoryInMemory(
  val artists:     Ref[Task, Map[UUID, ArtistDTO]],
  val userArtists: Ref[Task, Map[UUID, Set[UUID]]]
) extends ArtistDTORepository {

  override def create(artistDTO: ArtistDTO): Task[Unit] = artists.update(_.updated(artistDTO.id, artistDTO))

  override def findByID(id: UUID): Task[Option[ArtistDTO]] = artists.get.map(_.get(id))

  override def findByDeezerID(deezerID: Int): Task[Option[ArtistDTO]] =
    artists.get.map(_.values.find(_.deezerID.contains(deezerID)))

  override def setForUser(userID: UUID, artistsIDs: Set[UUID]): Task[Unit] =
    userArtists.update(_.updated(userID, artistsIDs))
}
object ArtistDTORepositoryInMemory {

  def create(): Task[ArtistDTORepositoryInMemory] =
    for {
      artists <- Ref.of[Task, Map[UUID, ArtistDTO]](Map.empty)
      userArtists <- Ref.of[Task, Map[UUID, Set[UUID]]](Map.empty)
    } yield new ArtistDTORepositoryInMemory(artists, userArtists)
}

class PostgresArtistDTORepository(transactor: Transactor[Task]) extends ArtistDTORepository with DBLogging {

  override def create(artistDTO: ArtistDTO): Task[Unit] =
    fr"""INSERT
          INTO artists(id,
                       deezer_id,
                       name)
          VALUES (${artistDTO.id},
                  ${artistDTO.deezerID},
                  ${artistDTO.name})""".update.run.transact[Task](transactor).void.improveLogging("create-artist")

  override def findByID(id: UUID): Task[Option[ArtistDTO]] =
    fr"""SELECT id, deezer_id, name
         FROM artists
         WHERE id = $id""".query[ArtistDTO].option.transact[Task](transactor).improveLogging("find-artist-by-id")

  override def findByDeezerID(deezerID: Int): Task[Option[ArtistDTO]] =
    fr"""SELECT id, deezer_id, name
         FROM artists
         WHERE deezer_id = $deezerID"""
      .query[ArtistDTO]
      .option
      .transact[Task](transactor)
      .improveLogging("find-artist-by-deezer-id")

  override def setForUser(userID: UUID, artistIDs: Set[UUID]): Task[Unit] =
    fr"""INSERT
         INTO user_artists(user_id, artist_ids)
         VALUES (${userID}, ${artistIDs.toArray[UUID]})
         ON CONFLICT (user_id) DO
           UPDATE SET artist_ids = ${artistIDs
      .toArray[UUID]}""".update.run.transact[Task](transactor).void.improveLogging("set-artist-for-user")
}
