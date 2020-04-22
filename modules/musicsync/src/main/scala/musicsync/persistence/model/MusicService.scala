package musicsync.persistence.model

import enumeratum._

sealed trait MusicService extends EnumEntry
object MusicService extends Enum[MusicService] {
  case object Deezer extends MusicService

  override def values: IndexedSeq[MusicService] = findValues
}
