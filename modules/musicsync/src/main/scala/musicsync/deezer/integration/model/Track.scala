package musicsync.deezer.integration.model

import java.net.URI
import java.time.LocalDate

import cats.implicits._
import io.circe.{ Decoder, Encoder }
import io.circe.generic.extras.ConfiguredJsonCodec
import musicsync.domain.{ model => dm }

sealed trait Track {
  val id:             Int
  val readable:       Boolean
  val title:          String
  val link:           URI
  val duration:       Int
  val rank:           Int
  val explicitLyrics: Boolean

  def toDomain(trackID: dm.ID[dm.Track], artistID: dm.ID[dm.Artist]): Either[String, dm.Track] =
    (Right(trackID),
     dm.DeezerID.parse[dm.Track](id).map(Option(_)),
     Right(artistID),
     dm.Title.parse[dm.Track](title),
     dm.Duration.parse(duration)).mapN(dm.Track.apply)
}

@ConfiguredJsonCodec
final case class SummaryTrack(
  id:             Int,
  readable:       Boolean,
  title:          String,
  link:           URI,
  duration:       Int,
  rank:           Int,
  explicitLyrics: Boolean
) extends Track

@ConfiguredJsonCodec
final case class FullTrack(
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
  alternative:           Option[Track],
  contributors:          List[Artist],
  artist:                Artist,
  album:                 Album
) extends Track

object Track {

  implicit val decoder: Decoder[Track] = Decoder[FullTrack].widen or Decoder[SummaryTrack].widen
  implicit val encoder: Encoder[Track] = {
    case track: SummaryTrack => Encoder[SummaryTrack].apply(track)
    case track: FullTrack    => Encoder[FullTrack].apply(track)
  }
}
