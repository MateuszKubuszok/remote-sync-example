import sbt._
import Settings._

lazy val root = project.root
  .setName("remote-sync-example")
  .setDescription("Deezer to Database sync")
  .configureRoot
  .aggregate(musicsync)

lazy val musicsync = project.from("musicsync")
  .setName("musicsync")
  .setDescription("Synchronize Deezer for user's data with a database")
  .setInitialImport()
  .configureModule
  .configureTests()
  .settings(Compile / resourceGenerators += task[Seq[File]] {
    val file = (Compile / resourceManaged).value / "remote-sync-example-version.conf"
    IO.write(file, s"version=${version.value}")
    Seq(file)
  })

addCommandAlias("fullTest", ";test;scalastyle")
