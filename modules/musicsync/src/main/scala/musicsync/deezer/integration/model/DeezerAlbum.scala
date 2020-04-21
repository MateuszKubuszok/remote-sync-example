package musicsync.deezer.integration.model

import java.net.URI
import java.time.LocalDate

import cats.implicits._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{ Decoder, Encoder }
import musicsync.domain.model._

sealed trait DeezerAlbum {
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
  val artist:         DeezerArtist

  def toDomain(albumID: ID[Album], artistID: ID[Artist]): Either[String, Album] =
    (Right(albumID),
     DeezerID.parse[Album](id).map(Option(_)),
     Right(artistID),
     Title.parse[Album](title),
     ReleaseDate.parse(releaseDate)).mapN(Album.apply)
}

@ConfiguredJsonCodec
final case class SummaryDeezerAlbum(
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
  artist:         DeezerArtist
) extends DeezerAlbum

@ConfiguredJsonCodec
final case class FullDeezerAlbum(
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
  contributors:          List[SummaryDeezerArtist],
  artist:                DeezerArtist,
  tracks:                DeezerTracks
) extends DeezerAlbum

object DeezerAlbum {

  implicit val decoder: Decoder[DeezerAlbum] = Decoder[FullDeezerAlbum].widen or Decoder[SummaryDeezerAlbum].widen
  implicit val encoder: Encoder[DeezerAlbum] = {
    case track: SummaryDeezerAlbum => Encoder[SummaryDeezerAlbum].apply(track)
    case track: FullDeezerAlbum    => Encoder[FullDeezerAlbum].apply(track)
  }
}
