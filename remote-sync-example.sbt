import sbt._
import Settings._

lazy val root = project.root
  .setName("remote-sync-example")
  .setDescription("Deezer to Database sync")
  .configureRoot
  .aggregate(common, first, second)

lazy val common = project.from("common")
  .setName("common")
  .setDescription("Common utilities")
  .setInitialImport()
  .configureModule
  .configureTests()
  .settings(Compile / resourceGenerators += task[Seq[File]] {
    val file = (Compile / resourceManaged).value / "remote-sync-example-version.conf"
    IO.write(file, s"version=${version.value}")
    Seq(file)
  })

lazy val first = project.from("first")
  .setName("first")
  .setDescription("First project")
  .setInitialImport("musicsync.first._")
  .configureModule
  .configureTests()
  .compileAndTestDependsOn(common)

lazy val second = project.from("second")
  .setName("second")
  .setDescription("Second project")
  .setInitialImport("musicsync.second._")
  .configureModule
  .configureTests()
  .compileAndTestDependsOn(common)

addCommandAlias("fullTest", ";test;scalastyle")
