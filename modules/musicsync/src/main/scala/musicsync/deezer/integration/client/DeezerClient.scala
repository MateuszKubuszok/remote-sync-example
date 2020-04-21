package musicsync.deezer.integration.client

import java.net.URI

import endpoints._
import monix.eval.Task
import musicsync.deezer.integration.model
import musicsync.deezer.integration.model.DeezerApi
import com.softwaremill.sttp.okhttp.monix.OkHttpMonixBackend

class DeezerClient(
  backend: OkHttpMonixBackend,
  host:    URI = new URI("https://api.deezer.com/")
) extends sttp.client.Endpoints[Task](host.toString, backend)
    with sttp.client.JsonEntitiesFromCodecs[Task]
    with DeezerApi {

  override protected implicit val accessTokenQueryString: model.AccessToken => List[String] = m => List(m.value)
}
