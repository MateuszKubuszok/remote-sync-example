package musicsync.deezer.integration.model

import java.net.URI

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
final case class Albums(
  data:     List[Album],
  checksum: Option[String],
  total:    Option[Int],
  next:     Option[URI]
)
