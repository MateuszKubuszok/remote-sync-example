package musicsync.domain

import java.time.LocalDate
import java.util.UUID

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.NonNegative
import io.estatico.newtype.macros.newtype

package object model {

  @newtype case class ID[Entity](value: UUID)
  object ID {
    def parse[Entity](uuid: UUID): Either[String, ID[Entity]] = Right(ID(uuid))
  }

  @newtype case class DeezerID[Entity](value: Int)
  object DeezerID {
    def parse[Entity](deezerID: Int): Either[String, DeezerID[Entity]] = Right(DeezerID(deezerID))
  }

  @newtype case class Title[Entity](value: String Refined NonEmpty)
  object Title {
    def parse[Entity](title: String): Either[String, Title[Entity]] = refineV[NonEmpty](title).map(Title(_))
  }

  @newtype case class Name(value: String Refined NonEmpty)
  object Name {
    def parse(name: String): Either[String, Name] = refineV[NonEmpty](name).map(Name(_))
  }

  @newtype case class ReleaseDate(value: LocalDate)
  object ReleaseDate {
    def parse(releaseDate: LocalDate): Either[String, ReleaseDate] = Right(ReleaseDate(releaseDate))
  }
  @newtype case class Duration(value: Int Refined NonNegative)
  object Duration {
    def parse(duration: Int): Either[String, Duration] = refineV[NonNegative](duration).map(Duration(_))
  }
}
