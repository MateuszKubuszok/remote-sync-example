package musicsync.deezer.integration.model

import java.nio.file._

import io.circe.parser._
import org.specs2.mutable.Specification

class CodecsSpec extends Specification {

  private def readJson(name: String) =
    new String(Files.readAllBytes(Paths.get(getClass.getClassLoader.getResource(name).getPath)))

  "Album codecs" should {
    "deserialize example" in {
      decode[DeezerAlbum](readJson("album.json")) must beRight
    }
  }

  "Albums codecs" should {
    "deserialize example" in {
      decode[DeezerAlbums](readJson("albums.json")) must beRight
    }
  }

  "Artists codecs" should {
    "deserialize example" in {
      decode[DeezerArtists](readJson("artists.json")) must beRight
    }
  }

  "Track codecs" should {
    "deserialize example" in {
      decode[DeezerTrack](readJson("track.json")) must beRight
    }
  }

  "Tracks codecs" should {
    "deserialize example" in {
      decode[DeezerTracks](readJson("tracks.json")) must beRight
    }
  }
}
