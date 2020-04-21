package musicsync.deezer.integration.model

import java.net.URI

import cats.implicits._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{ Decoder, Encoder }
import musicsync.domain.{ model => dm }

sealed trait Artist {
  val id:           Int
  val name:         String
  val picture:      URI
  val pictureSmall: URI
  val pictureBig:   URI
  val pictureXL:    URI
  val tracklist:    URI

  def toDomain(artistID: dm.ID[dm.Artist]): Either[String, dm.Artist] =
    (Right(artistID), dm.DeezerID.parse[dm.Artist](id).map(Option(_)), dm.Name.parse(name)).mapN(dm.Artist.apply)
}

@ConfiguredJsonCodec
final case class SummaryArtist(
  id:           Int,
  name:         String,
  picture:      URI,
  pictureSmall: URI,
  pictureBig:   URI,
  pictureXL:    URI,
  tracklist:    URI
) extends Artist

@ConfiguredJsonCodec
final case class FullArtist(
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
) extends Artist

object Artist {

  implicit val decoder: Decoder[Artist] = Decoder[FullArtist].widen or Decoder[SummaryArtist].widen
  implicit val encoder: Encoder[Artist] = {
    case track: SummaryArtist => Encoder[SummaryArtist].apply(track)
    case track: FullArtist    => Encoder[FullArtist].apply(track)
  }
}
