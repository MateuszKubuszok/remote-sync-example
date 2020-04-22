package musicsync.persistence.repositories

import java.util.UUID

import cats.effect.concurrent.Ref
import monix.eval.Task
import musicsync.persistence.model.ArtistDTO

trait ArtistDTORepository {

  def create(artistDTO: ArtistDTO): Task[Unit]

  def findByID(id: UUID): Task[Option[ArtistDTO]]

  def findByDeezerID(deezerID: Int): Task[Option[ArtistDTO]]

  def setForUser(userID: UUID, artistsIDs: Set[UUID]): Task[Unit]
}

class ArtistDTORepositoryInMemory(
  artists:     Ref[Task, Map[UUID, ArtistDTO]],
  userArtists: Ref[Task, Map[UUID, Set[UUID]]]
) extends ArtistDTORepository {

  override def create(artistDTO: ArtistDTO): Task[Unit] = artists.update(_.updated(artistDTO.id, artistDTO))

  override def findByID(id: UUID): Task[Option[ArtistDTO]] = artists.get.map(_.get(id))

  override def findByDeezerID(deezerID: Int): Task[Option[ArtistDTO]] =
    artists.get.map(_.values.find(_.deezerID.contains(deezerID)))

  override def setForUser(userID: UUID, artistsIDs: Set[UUID]): Task[Unit] =
    userArtists.update(_.updated(userID, artistsIDs))
}
object ArtistDTORepositoryInMemory {

  def create(): Task[ArtistDTORepository] =
    for {
      artists <- Ref.of[Task, Map[UUID, ArtistDTO]](Map.empty)
      userArtists <- Ref.of[Task, Map[UUID, Set[UUID]]](Map.empty)
    } yield new ArtistDTORepositoryInMemory(artists, userArtists)
}
