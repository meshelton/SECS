lazy val core = (project in file("./core"))
  .settings(
    name := "secs",
    organization := "io.github.meshelton",
    version := "0.1",
    scalaVersion := "2.12.4",
    fork := true,
    scalacOptions ++= Seq("-feature", "-deprecation"),
  )

lazy val example = (project in file("./example"))
  .settings(
      name := "secs-example",
      organization := "io.github.meshelton",
      version := "0.1",
      scalaVersion := "2.12.4",
      fork := true,
      scalacOptions ++= Seq("-feature", "-deprecation"),
  ).dependsOn(core)