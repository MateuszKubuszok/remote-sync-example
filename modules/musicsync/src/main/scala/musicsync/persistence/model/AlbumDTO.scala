package musicsync.persistence.model

import java.time.LocalDate
import java.util.UUID

import cats.implicits._
import musicsync.domain.model._

final case class AlbumDTO(
  id:          UUID,
  deezerID:    Option[Int],
  artistID:    UUID,
  title:       String,
  releaseDate: LocalDate
) {

  def toDomain: Either[String, Album] =
    (ID.parse[Album](id),
     deezerID.traverse(DeezerID.parse[Album]),
     ID.parse[Artist](artistID),
     Title.parse[Album](title),
     ReleaseDate.parse(releaseDate)).mapN(Album.apply)
}
