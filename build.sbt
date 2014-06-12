organization := "com.shelton"

name := "secs"

version := "0.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

libraryDependencies ++= {
  val scalazVersion = "7.0.6"
  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion
  )
}
