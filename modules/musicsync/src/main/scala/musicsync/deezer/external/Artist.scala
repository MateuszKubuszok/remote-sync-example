package musicsync.deezer.external

import java.net.URI

import cats.syntax.functor._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{ Decoder, Encoder }

sealed trait Artist {
  val id:           Int
  val name:         String
  val picture:      URI
  val pictureSmall: URI
  val pictureBig:   URI
  val pictureXL:    URI
  val tracklist:    URI
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
