package musicsync.deezer.external

import java.net.URI
import java.time.LocalDate

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
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
)
