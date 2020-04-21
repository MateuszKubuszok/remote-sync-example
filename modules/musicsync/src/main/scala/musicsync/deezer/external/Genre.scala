package musicsync.deezer.external

import java.net.URI

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
final case class Genre(
  id:      Int,
  name:    String,
  picture: URI
)
