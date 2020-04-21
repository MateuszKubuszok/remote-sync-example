package musicsync.persistence.model

import java.util.UUID

import musicsync.domain.model._

final case class UserDTO(
  id: UUID
) {

  def toDomain: Either[String, User] =
    ID.parse[User](id).map(User)
}
