package musicsync.deezer.integration.model

import java.net.URI

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
final case class DeezerArtists(
  data:     List[DeezerArtist],
  checksum: Option[String],
  total:    Option[Int],
  next:     Option[URI]
)
