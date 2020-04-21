package musicsync.domain.model

final case class Track(
  id:       ID[Track],
  deezerID: Option[DeezerID[Track]],
  artistID: ID[Artist],
  title:    Title[Track],
  duration: Duration
)
