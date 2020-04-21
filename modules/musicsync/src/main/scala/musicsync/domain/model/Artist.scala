package musicsync.domain.model

final case class Artist(
  id:       ID[Artist],
  deezerID: Option[DeezerID[Artist]],
  name:     Name
)
