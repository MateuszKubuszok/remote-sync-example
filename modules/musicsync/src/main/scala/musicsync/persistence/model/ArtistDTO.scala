package musicsync.persistence.model

import java.util.UUID

import cats.implicits._
import musicsync.domain.model._

final case class ArtistDTO(
  id:       UUID,
  deezerID: Option[Int],
  name:     String
) {

  def toDomain(artistID: ID[Artist]): Either[String, Artist] =
    (ID.parse[Artist](id), deezerID.traverse(DeezerID.parse[Artist]), Name.parse(name)).mapN(Artist.apply)
}
