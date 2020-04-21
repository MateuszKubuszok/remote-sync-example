package musicsync.deezer.external

import java.net.URI
import java.time.LocalDateTime

import io.circe.generic.JsonCodec

@JsonCodec
final case class Album(
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
  genres:                List[Genre],
  label:                 String,
  nbTracks:              Int,
  duration:              Int,
  releaseDate:           LocalDateTime,
  recordType:            String, // TODO: consider enum
  available:             Boolean,
  tracklist:             URI,
  explicitLyrics:        Boolean,
  explicitContentLyrics: ExplicitContentLevel,
  explicitContentCover:  ExplicitContentLevel,
  contributors:          List[Artist],
  artist:                Artist,
  tracks:                List[SummaryTrack]
)
