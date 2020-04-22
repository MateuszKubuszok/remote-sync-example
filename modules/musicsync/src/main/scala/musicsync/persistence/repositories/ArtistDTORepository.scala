package musicsync.persistence.repositories

import java.util.UUID

import cats.effect.concurrent.Ref
import monix.eval.Task
import musicsync.persistence.model.ArtistDTO

trait ArtistDTORepository {

  def create(artistDTO: ArtistDTO): Task[Unit]

  def findByID(id: UUID): Task[Option[ArtistDTO]]

  def findByDeezerID(deezerID: Int): Task[Option[ArtistDTO]]

  def addToUser(userID: UUID, artistID: UUID): Task[Unit]

  def removeFromUser(userID: UUID, artistID: UUID): Task[Unit]
}

class ArtistDTORepositoryInMemory(
  artists:     Ref[Task, Map[UUID, ArtistDTO]],
  userArtists: Ref[Task, Map[UUID, Set[UUID]]]
) extends ArtistDTORepository {

  override def create(artistDTO: ArtistDTO): Task[Unit] = artists.update(_.updated(artistDTO.id, artistDTO))

  override def findByID(id: UUID): Task[Option[ArtistDTO]] = artists.get.map(_.get(id))

  override def findByDeezerID(deezerID: Int): Task[Option[ArtistDTO]] =
    artists.get.map(_.values.find(_.deezerID.contains(deezerID)))

  override def addToUser(userID: UUID, artistID: UUID): Task[Unit] =
    userArtists.update(_.updatedWith(userID) {
      case Some(ids) => Option(ids + artistID)
      case None      => Option(Set(artistID))
    })

  override def removeFromUser(userID: UUID, artistID: UUID): Task[Unit] =
    userArtists.update(_.updatedWith(userID) {
      case Some(ids) => Option(ids - artistID)
      case None      => None
    })
}
object ArtistDTORepositoryInMemory {

  def create(): Task[ArtistDTORepository] =
    for {
      artists <- Ref.of[Task, Map[UUID, ArtistDTO]](Map.empty)
      userArtists <- Ref.of[Task, Map[UUID, Set[UUID]]](Map.empty)
    } yield new ArtistDTORepositoryInMemory(artists, userArtists)
}
