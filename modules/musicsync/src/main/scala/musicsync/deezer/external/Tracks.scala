package musicsync.deezer.external

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
final case class Tracks(data: List[SummaryTrack])
