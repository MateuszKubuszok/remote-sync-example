package musicsync.persistence.repositories

import java.util.UUID

import cats.effect.concurrent.Ref
import monix.eval.Task
import musicsync.persistence.model.AlbumDTO

trait AlbumDTORepository {

  def create(albumDTO: AlbumDTO): Task[Unit]

  def findByID(id: UUID): Task[Option[AlbumDTO]]

  def findByDeezerID(deezerID: Int): Task[Option[AlbumDTO]]

  def setForUser(userID: UUID, albumIDs: Set[UUID]): Task[Unit]
}

class AlbumDTORepositoryInMemory(albums: Ref[Task, Map[UUID, AlbumDTO]], userAlbums: Ref[Task, Map[UUID, Set[UUID]]])
    extends AlbumDTORepository {

  override def create(albumDTO: AlbumDTO): Task[Unit] = albums.update(_.updated(albumDTO.id, albumDTO))

  override def findByID(id: UUID): Task[Option[AlbumDTO]] = albums.get.map(_.get(id))

  override def findByDeezerID(deezerID: Int): Task[Option[AlbumDTO]] =
    albums.get.map(_.values.find(_.deezerID.contains(deezerID)))

  override def setForUser(userID: UUID, albumIDs: Set[UUID]): Task[Unit] =
    userAlbums.update(_.updated(userID, albumIDs))
}
object AlbumDTORepositoryInMemory {

  def create(): Task[AlbumDTORepository] =
    for {
      albums <- Ref.of[Task, Map[UUID, AlbumDTO]](Map.empty)
      userAlbums <- Ref.of[Task, Map[UUID, Set[UUID]]](Map.empty)
    } yield new AlbumDTORepositoryInMemory(albums, userAlbums)
}
