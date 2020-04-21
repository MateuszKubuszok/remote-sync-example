package musicsync.persistence.model

import java.util.UUID

import cats.implicits._
import musicsync.domain.model._

final case class TrackDTO(
  id:       UUID,
  deezerID: Option[Int],
  artistID: UUID,
  title:    String,
  duration: Int
) {

  def toDomain: Either[String, Track] =
    (ID.parse[Track](id),
     deezerID.traverse(DeezerID.parse[Track]),
     ID.parse[Artist](artistID),
     Title.parse[Track](title),
     Duration.parse(duration)).mapN(Track.apply)
}
object TrackDTO {

  def fromDomain(track: Track): TrackDTO = TrackDTO(
    id       = track.id.value,
    deezerID = track.deezerID.map(_.value),
    artistID = track.artistID.value,
    title    = track.title.value.value,
    duration = track.duration.value.value
  )
}
