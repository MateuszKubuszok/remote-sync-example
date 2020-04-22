package musicsync.deezer.services

import java.net.URI

import cats.instances.string._
import cats.syntax.eq._
import fs2._
import monix.eval.Task
import musicsync.deezer.integration.client.DeezerClient
import musicsync.deezer.integration.model._

class DeezerStreams(client: DeezerClient) {

  private def parseQuery(next: URI): Option[Int] =
    next.getQuery
      .split('&')
      .map { pair =>
        val key :: value :: Nil = pair.split('=').toList
        key -> value
      }
      .find(_._1 === "index")
      .map(_._2.toInt)

  private def fetchAll[Group, Single](
    fetchBatch:  Option[Int] => Task[Group],
    extractSeq:  Group => Seq[Single],
    extractNext: Group => Option[URI]
  ): Stream[Task, Single] =
    Stream
      .unfoldLoopEval[Task, Int, Seq[Single]](0) { offset =>
        fetchBatch(Option(offset).filter(_ > 0)).map { group =>
          (extractSeq(group)) -> (extractNext(group).flatMap(parseQuery))
        }
      }
      .flatMap(seq => Stream(seq: _*))

  // public for debugging purposes

  def usersAlbums(accessToken: AccessToken): Stream[Task, DeezerAlbum] =
    fetchAll[DeezerAlbums, DeezerAlbum](offset => client.user.albums(accessToken -> offset), _.data.toSeq, _.next)

  def usersArtists(accessToken: AccessToken): Stream[Task, DeezerArtist] =
    fetchAll[DeezerArtists, DeezerArtist](offset => client.user.artists(accessToken -> offset), _.data.toSeq, _.next)

  def usersTracks(accessToken: AccessToken): Stream[Task, DeezerTrack] =
    fetchAll[DeezerTracks, DeezerTrack](offset => client.user.tracks(accessToken -> offset), _.data.toSeq, _.next)
}
