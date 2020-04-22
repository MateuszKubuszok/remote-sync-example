package musicsync.persistence.repositories

import java.util.UUID

import cats.implicits._
import cats.effect.concurrent.Ref
import doobie._
import doobie.implicits._
import doobie.implicits.legacy.localdate._
import doobie.postgres._
import doobie.postgres.implicits._
import monix.eval.Task
import musicsync.persistence.model.AlbumDTO

trait AlbumDTORepository {

  def create(albumDTO: AlbumDTO): Task[Unit]

  def findByID(id: UUID): Task[Option[AlbumDTO]]

  def findByDeezerID(deezerID: Int): Task[Option[AlbumDTO]]

  def setForUser(userID: UUID, albumIDs: Set[UUID]): Task[Unit]
}

class AlbumDTORepositoryInMemory(
  val albums:     Ref[Task, Map[UUID, AlbumDTO]],
  val userAlbums: Ref[Task, Map[UUID, Set[UUID]]]
) extends AlbumDTORepository {

  override def create(albumDTO: AlbumDTO): Task[Unit] = albums.update(_.updated(albumDTO.id, albumDTO))

  override def findByID(id: UUID): Task[Option[AlbumDTO]] = albums.get.map(_.get(id))

  override def findByDeezerID(deezerID: Int): Task[Option[AlbumDTO]] =
    albums.get.map(_.values.find(_.deezerID.contains(deezerID)))

  override def setForUser(userID: UUID, albumIDs: Set[UUID]): Task[Unit] =
    userAlbums.update(_.updated(userID, albumIDs))
}
object AlbumDTORepositoryInMemory {

  def create(): Task[AlbumDTORepositoryInMemory] =
    for {
      albums <- Ref.of[Task, Map[UUID, AlbumDTO]](Map.empty)
      userAlbums <- Ref.of[Task, Map[UUID, Set[UUID]]](Map.empty)
    } yield new AlbumDTORepositoryInMemory(albums, userAlbums)
}

class PostgresAlbumDTORepository(transactor: Transactor[Task]) extends AlbumDTORepository with DBLogging {

  override def create(albumDTO: AlbumDTO): Task[Unit] =
    fr"""INSERT
          INTO albums(id,
                      deezer_id,
                      artist_id,
                      title,
                      release_date)
          VALUES (${albumDTO.id},
                  ${albumDTO.deezerID},
                  ${albumDTO.artistID},
                  ${albumDTO.title},
                  ${albumDTO.releaseDate})""".update.run.transact[Task](transactor).void.improveLogging("create-album")

  override def findByID(id: UUID): Task[Option[AlbumDTO]] =
    fr"""SELECT id, deezer_id, artist_id, title, release_date
         FROM artists
         WHERE id = $id""".query[AlbumDTO].option.transact[Task](transactor).improveLogging("find-album-by-id")

  override def findByDeezerID(deezerID: Int): Task[Option[AlbumDTO]] =
    fr"""SELECT id, deezer_id, artist_id, title, release_date
         FROM artists
         WHERE deezer_id = $deezerID"""
      .query[AlbumDTO]
      .option
      .transact[Task](transactor)
      .improveLogging("find-album-by-deezer-id")

  override def setForUser(userID: UUID, albumIDs: Set[UUID]): Task[Unit] =
    fr"""INSERT
         INTO user_albums(user_id, album_ids)
         VALUES (${userID}, ${albumIDs.toArray[UUID]})
         ON CONFLICT (user_id) DO
           UPDATE SET album_ids = ${albumIDs
      .toArray[UUID]}""".update.run.transact[Task](transactor).void.improveLogging("set-album-for-user")
}
