package musicsync.deezer.integration

import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.circe.{ Decoder, Encoder }
import io.circe.generic.extras.Configuration
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import pureconfig.ConfigReader

import scala.util.Try

package object model {

  private[model] implicit val configuration: Configuration =
    Configuration.default.withDiscriminator("type").withSnakeCaseMemberNames.withSnakeCaseMemberNames

  val deezerLocalDateFormat: DateTimeFormatter = DateTimeFormatter.ISO_DATE

  private[model] implicit val localDateDecoder: Decoder[LocalDate] =
    Decoder.decodeString.emapTry[LocalDate](s => Try(LocalDate.parse(s, deezerLocalDateFormat)))
  private[model] implicit val localDateEncoder: Encoder[LocalDate] =
    Encoder.encodeString.contramap[LocalDate](_.format(deezerLocalDateFormat))

  private[model] implicit val uriDecoder: Decoder[URI] = Decoder.decodeString.emapTry[URI](s => Try(URI.create(s)))
  private[model] implicit val uriEncoder: Encoder[URI] = Encoder.encodeString.contramap[URI](_.toString)

  @newtype case class AccessToken(value: String)
  object AccessToken {
    implicit val configReader: ConfigReader[AccessToken] = ConfigReader[String].coerce
  }
}
