package musicsync.deezer.integration.model

import java.net.URI
import java.time.LocalDate

import cats.implicits._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{ Decoder, Encoder }
import musicsync.domain.{ model => dm }

sealed trait Album {
  val id:             Int
  val title:          String
  val link:           URI
  val cover:          URI
  val coverSmall:     URI
  val coverMedium:    URI
  val coverBig:       URI
  val coverXL:        URI
  val nbTracks:       Int
  val releaseDate:    LocalDate
  val recordType:     String
  val available:      Boolean
  val tracklist:      URI
  val explicitLyrics: Boolean
  val artist:         Artist

  def toDomain(albumID: dm.ID[dm.Album], artistID: dm.ID[dm.Artist]): Either[String, dm.Album] =
    (Right(albumID),
     dm.DeezerID.parse[dm.Album](id).map(Option(_)),
     Right(artistID),
     dm.Title.parse[dm.Album](title),
     dm.ReleaseDate.parse(releaseDate)).mapN(dm.Album.apply)
}

@ConfiguredJsonCodec
final case class SummaryAlbum(
  id:             Int,
  title:          String,
  link:           URI,
  cover:          URI,
  coverSmall:     URI,
  coverMedium:    URI,
  coverBig:       URI,
  coverXL:        URI,
  nbTracks:       Int,
  releaseDate:    LocalDate,
  recordType:     String, // TODO: consider enum
  available:      Boolean,
  tracklist:      URI,
  explicitLyrics: Boolean,
  artist:         Artist
) extends Album

@ConfiguredJsonCodec
final case class FullAlbum(
  id:                    Int,
  title:                 String,
  upc:                   String,
  share:                 URI,
  link:                  URI,
  cover:                 URI,
  coverSmall:            URI,
  coverMedium:           URI,
  coverBig:              URI,
  coverXL:               URI,
  genreID:               Int,
  genres:                Genres,
  label:                 String,
  nbTracks:              Int,
  duration:              Int,
  releaseDate:           LocalDate,
  recordType:            String, // TODO: consider enum
  available:             Boolean,
  tracklist:             URI,
  explicitLyrics:        Boolean,
  explicitContentLyrics: ExplicitContentLevel,
  explicitContentCover:  ExplicitContentLevel,
  contributors:          List[SummaryArtist],
  artist:                Artist,
  tracks:                Tracks
) extends Album

object Album {

  implicit val decoder: Decoder[Album] = Decoder[FullAlbum].widen or Decoder[SummaryAlbum].widen
  implicit val encoder: Encoder[Album] = {
    case track: SummaryAlbum => Encoder[SummaryAlbum].apply(track)
    case track: FullAlbum    => Encoder[FullAlbum].apply(track)
  }
}
