package musicsync.domain.model

final case class Album(
  id:          ID[Album],
  deezerID:    Option[DeezerID[Album]],
  artistID:    ID[Artist],
  title:       Title[Album],
  releaseDate: ReleaseDate
)
