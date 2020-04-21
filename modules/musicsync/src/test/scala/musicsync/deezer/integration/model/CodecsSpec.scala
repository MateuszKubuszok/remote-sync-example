package musicsync.deezer.integration.model

import java.nio.file._

import io.circe.parser._
import org.specs2.mutable.Specification

class CodecsSpec extends Specification {

  private def readJson(name: String) =
    new String(Files.readAllBytes(Paths.get(getClass.getClassLoader.getResource(name).getPath)))

  "Album codecs" should {
    "deserialize example" in {
      decode[Album](readJson("album.json")) must beRight
    }
  }

  "Albums codecs" should {
    "deserialize example" in {
      decode[Albums](readJson("albums.json")) must beRight
    }
  }

  "Artists codecs" should {
    "deserialize example" in {
      decode[Artists](readJson("artists.json")) must beRight
    }
  }

  "Track codecs" should {
    "deserialize example" in {
      decode[Track](readJson("track.json")) must beRight
    }
  }

  "Tracks codecs" should {
    "deserialize example" in {
      decode[Tracks](readJson("tracks.json")) must beRight
    }
  }
}
