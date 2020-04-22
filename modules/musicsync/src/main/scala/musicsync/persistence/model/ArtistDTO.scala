package musicsync.persistence.model

import java.util.UUID

import cats.implicits._
import musicsync.domain.model._

final case class ArtistDTO(
  id:       UUID,
  deezerID: Option[Int],
  name:     String
) {

  def toDomain: Either[String, Artist] =
    (ID.parse[Artist](id), deezerID.traverse(DeezerID.parse[Artist]), Name.parse(name)).mapN(Artist.apply)
}
object ArtistDTO {

  def fromDomain(artist: Artist): ArtistDTO = ArtistDTO(
    id       = artist.id.value,
    deezerID = artist.deezerID.map(_.value),
    name     = artist.name.value.value
  )
}
