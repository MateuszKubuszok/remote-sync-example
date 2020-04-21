package musicsync.deezer.external

import java.net.URI

import io.circe.generic.JsonCodec

@JsonCodec
final case class Artist(
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
)
