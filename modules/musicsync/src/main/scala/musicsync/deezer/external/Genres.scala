package musicsync.deezer.external

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
final case class Genres(data: List[Genre])
