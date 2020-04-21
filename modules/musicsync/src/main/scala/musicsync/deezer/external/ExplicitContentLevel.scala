package musicsync.deezer.external

import cats.implicits._
import enumeratum._
import io.circe.{ Decoder, Encoder }

sealed abstract class ExplicitContentLevel(val level: Int) extends EnumEntry
object ExplicitContentLevel extends Enum[ExplicitContentLevel] {
  case object Explicit extends ExplicitContentLevel(1)
  case object Unknown extends ExplicitContentLevel(2)
  case object Edited extends ExplicitContentLevel(3)
  case object NoAdviceAvailable extends ExplicitContentLevel(6)

  override def values: IndexedSeq[ExplicitContentLevel] = findValues

  implicit val decoder: Decoder[ExplicitContentLevel] =
    Decoder.decodeInt.emap[ExplicitContentLevel] { i =>
      values.find(_.level === i) match {
        case Some(value) => Right(value)
        case None        => Left("Not a valid explicit level")
      }
    }
  implicit val encoder: Encoder[ExplicitContentLevel] = Encoder.encodeInt.contramap[ExplicitContentLevel](_.level)
}
