package musicsync.persistence.repositories

import java.util.UUID

import cats.effect.concurrent.Ref
import monix.eval.Task
import musicsync.persistence.model.TrackDTO

trait TrackDTORepository {

  def create(trackDTO: TrackDTO): Task[Unit]

  def findByID(id: UUID): Task[Option[TrackDTO]]

  def findByDeezerID(deezerID: Int): Task[Option[TrackDTO]]

  def addToUser(userID: UUID, trackID: UUID): Task[Unit]

  def removeFromUser(userID: UUID, trackID: UUID): Task[Unit]
}

class TrackDTORepositoryInMemory(tracks: Ref[Task, Map[UUID, TrackDTO]], userTracks: Ref[Task, Map[UUID, Set[UUID]]])
    extends TrackDTORepository {

  override def create(trackDTO: TrackDTO): Task[Unit] = tracks.update(_.updated(trackDTO.id, trackDTO))

  override def findByID(id: UUID): Task[Option[TrackDTO]] = tracks.get.map(_.get(id))

  override def findByDeezerID(deezerID: Int): Task[Option[TrackDTO]] =
    tracks.get.map(_.values.find(_.deezerID.contains(deezerID)))

  override def addToUser(userID: UUID, trackID: UUID): Task[Unit] =
    userTracks.update(_.updatedWith(userID) {
      case Some(ids) => Option(ids + trackID)
      case None      => Option(Set(trackID))
    })

  override def removeFromUser(userID: UUID, trackID: UUID): Task[Unit] =
    userTracks.update(_.updatedWith(userID) {
      case Some(ids) => Option(ids - trackID)
      case None      => None
    })
}
object TrackDTORepositoryInMemory {

  def create(): Task[TrackDTORepository] =
    for {
      tracks <- Ref.of[Task, Map[UUID, TrackDTO]](Map.empty)
      userTracks <- Ref.of[Task, Map[UUID, Set[UUID]]](Map.empty)
    } yield new TrackDTORepositoryInMemory(tracks, userTracks)
}
