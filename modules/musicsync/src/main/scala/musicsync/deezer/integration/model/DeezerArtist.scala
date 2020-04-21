package musicsync.deezer.integration.model

import java.net.URI

import cats.implicits._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{ Decoder, Encoder }
import musicsync.domain.model._

sealed trait DeezerArtist {
  val id:           Int
  val name:         String
  val picture:      URI
  val pictureSmall: URI
  val pictureBig:   URI
  val pictureXL:    URI
  val tracklist:    URI

  def toDomain(artistID: ID[Artist]): Either[String, Artist] =
    (Right(artistID), DeezerID.parse[Artist](id).map(Option(_)), Name.parse(name)).mapN(Artist.apply)
}

@ConfiguredJsonCodec
final case class SummaryDeezerArtist(
  id:           Int,
  name:         String,
  picture:      URI,
  pictureSmall: URI,
  pictureBig:   URI,
  pictureXL:    URI,
  tracklist:    URI
) extends DeezerArtist

@ConfiguredJsonCodec
final case class FullDeezerArtist(
  id:           Int,
  name:         String,
  link:         URI,
  share:        URI,
  picture:      URI,
  pictureSmall: URI,
  pictureBig:   URI,
  pictureXL:    URI,
  nbAlbum:      Int,
  nbFan:        Int,
  radio:        Boolean,
  tracklist:    URI
) extends DeezerArtist

object DeezerArtist {

  implicit val decoder: Decoder[DeezerArtist] = Decoder[FullDeezerArtist].widen or Decoder[SummaryDeezerArtist].widen
  implicit val encoder: Encoder[DeezerArtist] = {
    case track: SummaryDeezerArtist => Encoder[SummaryDeezerArtist].apply(track)
    case track: FullDeezerArtist    => Encoder[FullDeezerArtist].apply(track)
  }
}
