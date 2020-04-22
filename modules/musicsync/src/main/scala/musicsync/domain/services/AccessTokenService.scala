package musicsync.domain.services

import monix.eval.Task
import musicsync.deezer.integration.model.AccessToken
import musicsync.domain.model.{ ID, User }

trait AccessTokenService {

  def deezerAccessToken(user: ID[User]): Task[Option[AccessToken]]
}

class MockAccessTokenService(deezerAccessToken: AccessToken) extends AccessTokenService {

  override def deezerAccessToken(user: ID[User]): Task[Option[AccessToken]] = Task.pure(Option(deezerAccessToken))
}
