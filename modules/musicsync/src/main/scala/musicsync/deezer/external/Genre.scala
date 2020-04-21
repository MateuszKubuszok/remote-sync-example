package musicsync.deezer.external

import java.net.URI

import io.circe.generic.JsonCodec

@JsonCodec
final case class Genre(
  id:            Int,
  name:          String,
  picture:       URI,
  pictureSmall:  URI,
  pictureMedium: URI,
  pictureBig:    URI,
  pictureXL:     URI
)
