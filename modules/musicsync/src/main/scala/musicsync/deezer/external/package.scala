package musicsync.deezer

import java.net.URI
import java.time.{ LocalDate, LocalDateTime }
import java.time.format.DateTimeFormatter

import io.circe.{ Decoder, Encoder }
import io.estatico.newtype.macros.newtype

import scala.util.Try

package object external {

  val deezerLocalDateFormat:     DateTimeFormatter = DateTimeFormatter.ISO_DATE
  val deezerLocalDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

  private[external] implicit val localDateDecoder: Decoder[LocalDate] =
    Decoder.decodeString.emapTry[LocalDate](s => Try(LocalDate.parse(s, deezerLocalDateTimeFormat)))
  private[external] implicit val localDateEncoder: Encoder[LocalDate] =
    Encoder.encodeString.contramap[LocalDate](_.format(deezerLocalDateFormat))
  private[external] implicit val localDateTimeDecoder: Decoder[LocalDateTime] =
    Decoder.decodeString.emapTry[LocalDateTime](s => Try(LocalDateTime.parse(s, deezerLocalDateTimeFormat)))
  private[external] implicit val localDateTimeEncoder: Encoder[LocalDateTime] =
    Encoder.encodeString.contramap[LocalDateTime](_.format(deezerLocalDateFormat))

  private[external] implicit val uriDecoder: Decoder[URI] = Decoder.decodeString.emapTry[URI](s => Try(URI.create(s)))
  private[external] implicit val uriEncoder: Encoder[URI] = Encoder.encodeString.contramap[URI](_.toString)

  @newtype case class AccessToken(value: String)
  object AccessToken {}
}
