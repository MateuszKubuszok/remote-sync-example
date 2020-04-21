package musicsync.deezer.integration.model

import endpoints._
import io.circe.Json

trait DeezerApi
    extends algebra.Endpoints
    with algebra.JsonEntitiesFromCodecs
    with algebra.circe.JsonEntitiesFromCodecs {

  // path here should be https://api.deezer.com/

  protected implicit val accessTokenQueryString: QueryStringParam[AccessToken]

  object user {

    val albums: Endpoint[(AccessToken, Option[Int]), DeezerAlbums] = endpoint(
      get(path / "user" / "me" / "albums" /? (qs[AccessToken]("access_token") & qs[Option[Int]]("index"))),
      response(OK, jsonResponse[DeezerAlbums])
    )
    val albumsDebug: Endpoint[(AccessToken, Option[Int]), Json] = endpoint(
      get(path / "user" / "me" / "albums" /? (qs[AccessToken]("access_token") & qs[Option[Int]]("index"))),
      response(OK, jsonResponse[Json])
    )

    val artists: Endpoint[(AccessToken, Option[Int]), DeezerArtists] = endpoint(
      get(path / "user" / "me" / "artists" /? (qs[AccessToken]("access_token") & qs[Option[Int]]("index"))),
      response(OK, jsonResponse[DeezerArtists])
    )
    val artistsDebug: Endpoint[(AccessToken, Option[Int]), Json] = endpoint(
      get(path / "user" / "me" / "artists" /? (qs[AccessToken]("access_token") & qs[Option[Int]]("index"))),
      response(OK, jsonResponse[Json])
    )

    val tracks: Endpoint[(AccessToken, Option[Int]), DeezerTracks] = endpoint(
      get(path / "user" / "me" / "tracks" /? (qs[AccessToken]("access_token") & qs[Option[Int]]("index"))),
      response(OK, jsonResponse[DeezerTracks])
    )
    val tracksDebug: Endpoint[(AccessToken, Option[Int]), Json] = endpoint(
      get(path / "user" / "me" / "tracks" /? (qs[AccessToken]("access_token") & qs[Option[Int]]("index"))),
      response(OK, jsonResponse[Json])
    )
  }
}
