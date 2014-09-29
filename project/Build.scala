import sbt._
import Keys._
import sbtassembly.Plugin._ 
import AssemblyKeys._

object BuildSettings {
  val buildOrganization = "io.github.meshelton"
  val buildVersion      = "0.1"
  val buildScalaVersion = "2.11.2"
  val nativeLibraryPath = "./src/main/resources/gui/nativeLibs"

  val buildSettings = Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    fork := true,
    scalacOptions ++= Seq("-feature", "-deprecation")
  ) ++ assemblySettings
}

object Resolvers {
  val resolvers = Seq()
}

object Dependencies { }

object Group extends Build {
  import BuildSettings._
  import Resolvers._
  import Dependencies._

  lazy val secs = Project(
      "secs",
      file("core"),
      settings = buildSettings ++ Seq(
          name := "secs"
        )
    )

  lazy val secsExmaple = Project(
      "secs-example",
      file("example"),
      settings = buildSettings ++ Seq(
          name := "secs-example"
        )
    ) dependsOn(secs)

}
