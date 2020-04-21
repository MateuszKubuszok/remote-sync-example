package musicsync.deezer

import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.circe.{ Decoder, Encoder }
import io.circe.generic.extras.Configuration
import io.estatico.newtype.macros.newtype

import scala.util.Try

package object external {

  private[external] implicit val configuration: Configuration =
    Configuration.default.withDiscriminator("type").withSnakeCaseMemberNames.withSnakeCaseMemberNames

  val deezerLocalDateFormat: DateTimeFormatter = DateTimeFormatter.ISO_DATE

  private[external] implicit val localDateDecoder: Decoder[LocalDate] =
    Decoder.decodeString.emapTry[LocalDate](s => Try(LocalDate.parse(s, deezerLocalDateFormat)))
  private[external] implicit val localDateEncoder: Encoder[LocalDate] =
    Encoder.encodeString.contramap[LocalDate](_.format(deezerLocalDateFormat))

  private[external] implicit val uriDecoder: Decoder[URI] = Decoder.decodeString.emapTry[URI](s => Try(URI.create(s)))
  private[external] implicit val uriEncoder: Encoder[URI] = Encoder.encodeString.contramap[URI](_.toString)

  @newtype case class AccessToken(value: String)
}
