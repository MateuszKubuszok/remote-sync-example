package musicsync.persistence.model

import java.util.UUID

import musicsync.domain.model._

final case class UserDTO(
  id:                UUID,
  deezerAccessToken: Option[String]
) {

  def toDomain: Either[String, User] =
    ID.parse[User](id).map(User)
}
object UserDTO {

  def fromModel(user: User, deezerAccessToken: Option[String] = None): UserDTO = UserDTO(
    id                = user.id.value,
    deezerAccessToken = deezerAccessToken
  )
}
