package musicsync.persistence.repositories

import java.util.UUID

import cats.implicits._
import cats.effect.concurrent.Ref
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import monix.eval.Task
import musicsync.persistence.model.TrackDTO

trait TrackDTORepository {

  def create(trackDTO: TrackDTO): Task[Unit]

  def findByID(id: UUID): Task[Option[TrackDTO]]

  def findByDeezerID(deezerID: Int): Task[Option[TrackDTO]]

  def setForUser(userID: UUID, tracksIDs: Set[UUID]): Task[Unit]
}

class TrackDTORepositoryInMemory(
  val tracks:     Ref[Task, Map[UUID, TrackDTO]],
  val userTracks: Ref[Task, Map[UUID, Set[UUID]]]
) extends TrackDTORepository {

  override def create(trackDTO: TrackDTO): Task[Unit] = tracks.update(_.updated(trackDTO.id, trackDTO))

  override def findByID(id: UUID): Task[Option[TrackDTO]] = tracks.get.map(_.get(id))

  override def findByDeezerID(deezerID: Int): Task[Option[TrackDTO]] =
    tracks.get.map(_.values.find(_.deezerID.contains(deezerID)))

  override def setForUser(userID: UUID, tracksIDs: Set[UUID]): Task[Unit] =
    userTracks.update(_.updated(userID, tracksIDs))
}
object TrackDTORepositoryInMemory {

  def create(): Task[TrackDTORepositoryInMemory] =
    for {
      tracks <- Ref.of[Task, Map[UUID, TrackDTO]](Map.empty)
      userTracks <- Ref.of[Task, Map[UUID, Set[UUID]]](Map.empty)
    } yield new TrackDTORepositoryInMemory(tracks, userTracks)
}

class PostgresTrackDTORepository(transactor: Transactor[Task]) extends TrackDTORepository with DBLogging {

  override def create(trackDTO: TrackDTO): Task[Unit] =
    fr"""INSERT
          INTO tracks(id,
                      deezer_id,
                      artist_id,
                      title,
                      duration)
          VALUES (${trackDTO.id},
                  ${trackDTO.deezerID},
                  ${trackDTO.artistID},
                  ${trackDTO.title},
                  ${trackDTO.duration})""".update.run.transact[Task](transactor).void.improveLogging("create-track")

  override def findByID(id: UUID): Task[Option[TrackDTO]] =
    fr"""SELECT id, deezer_id, artist_id, title, duration
         FROM artists
         WHERE id = $id""".query[TrackDTO].option.transact[Task](transactor).improveLogging("find-track-by-id")

  override def findByDeezerID(deezerID: Int): Task[Option[TrackDTO]] =
    fr"""SELECT id, deezer_id, artist_id, title, duration
         FROM artists
         WHERE deezer_id = $deezerID"""
      .query[TrackDTO]
      .option
      .transact[Task](transactor)
      .improveLogging("find-track-by-deezer-id")

  override def setForUser(userID: UUID, trackIDs: Set[UUID]): Task[Unit] =
    fr"""INSERT
         INTO user_artists(user_id, track_ids)
         VALUES (${userID}, ${trackIDs.toArray[UUID]})
         ON CONFLICT (user_id) DO
           UPDATE SET track_ids = ${trackIDs
      .toArray[UUID]}""".update.run.transact[Task](transactor).void.improveLogging("set-track-for-user")
}
