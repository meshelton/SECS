import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "io.github.meshelton"
  val buildVersion      = "0.1"
  val buildScalaVersion = "2.11.1"

  val buildSettings = Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

object Resolvers {
  val sunrepo = "Sun Maven2 Repo" at "http://download.java.net/maven/2"

  val resolvers = Seq(sunrepo)
}

object Dependencies {
  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.4.1" % "test"
}

object SECSBuild extends Build {
  import BuildSettings._
  import Resolvers._
  import Dependencies._

  val secs = Project(
      "SECS",
      file("core"),
      settings = buildSettings ++ Seq(
          name := "SECS"
        )
    )
  val secsExmaple = Project(
      "SECS-Example",
      file("example"),
      settings = buildSettings ++ Seq(
          name := "SECS-Example"
        )
    ) dependsOn(secs)

}
