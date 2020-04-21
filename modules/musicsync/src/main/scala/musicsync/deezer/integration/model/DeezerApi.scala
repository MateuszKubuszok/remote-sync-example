package musicsync.deezer.integration.model

import endpoints._

trait DeezerApi
    extends algebra.Endpoints
    with algebra.JsonEntitiesFromCodecs
    with algebra.circe.JsonEntitiesFromCodecs {

  // path here should be https://api.deezer.com/

  protected implicit val accessTokenQueryString: QueryStringParam[AccessToken]

  object user {

    val albums: Endpoint[AccessToken, List[Album]] = endpoint[AccessToken, List[Album]](
      get(path / "user" / "albums" /? qs[AccessToken]("access_token")),
      response(OK, jsonResponse[List[Album]])
    )

    val artists: Endpoint[AccessToken, List[Artist]] = endpoint[AccessToken, List[Artist]](
      get(path / "user" / "artists" /? qs[AccessToken]("access_token")),
      response(OK, jsonResponse[List[Artist]])
    )

    val tracks: Endpoint[AccessToken, List[FullTrack]] = endpoint[AccessToken, List[FullTrack]](
      get(path / "user" / "tracks" /? qs[AccessToken]("access_token")),
      response(OK, jsonResponse[List[FullTrack]])
    )
  }
}
