package musicsync.deezer.integration.model

import java.net.URI
import java.time.LocalDate

import cats.implicits._
import io.circe.{ Decoder, Encoder }
import io.circe.generic.extras.ConfiguredJsonCodec
import musicsync.domain.model._

sealed trait DeezerTrack {
  val id:             Int
  val readable:       Boolean
  val title:          String
  val link:           URI
  val duration:       Int
  val rank:           Int
  val explicitLyrics: Boolean

  def toDomain(trackID: ID[Track], artistID: ID[Artist]): Either[String, Track] =
    (Right(trackID),
     DeezerID.parse[Track](id).map(Option(_)),
     Right(artistID),
     Title.parse[Track](title),
     Duration.parse(duration)).mapN(Track.apply)
}

@ConfiguredJsonCodec
final case class SummaryDeezerTrack(
  id:             Int,
  readable:       Boolean,
  title:          String,
  link:           URI,
  duration:       Int,
  rank:           Int,
  explicitLyrics: Boolean
) extends DeezerTrack

@ConfiguredJsonCodec
final case class FullDeezerTrack(
  id:                    Int,
  readable:              Boolean,
  title:                 String,
  titleShort:            String,
  titleVersion:          String,
  unseen:                Boolean,
  isrc:                  String,
  link:                  URI,
  share:                 URI,
  duration:              Int,
  trackPosition:         Int,
  diskNumber:            Int,
  rank:                  Int,
  releaseDate:           LocalDate,
  explicitLyrics:        Boolean,
  explicitContentLyrics: ExplicitContentLevel,
  explicitContentCover:  ExplicitContentLevel,
  preview:               URI,
  bmp:                   Double,
  alternative:           Option[DeezerTrack],
  contributors:          List[DeezerArtist],
  artist:                DeezerArtist,
  album:                 DeezerAlbum
) extends DeezerTrack

object DeezerTrack {

  implicit val decoder: Decoder[DeezerTrack] = Decoder[FullDeezerTrack].widen or Decoder[SummaryDeezerTrack].widen
  implicit val encoder: Encoder[DeezerTrack] = {
    case track: SummaryDeezerTrack => Encoder[SummaryDeezerTrack].apply(track)
    case track: FullDeezerTrack    => Encoder[FullDeezerTrack].apply(track)
  }
}
