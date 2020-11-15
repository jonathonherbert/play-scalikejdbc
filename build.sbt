name := """play-scala-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

resolvers ++= Seq(
  "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
  "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % scalikejdbcPlayVersion,
  "org.scalikejdbc" %% "scalikejdbc-test" % "3.5.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "org.skinny-framework" %% "skinny-orm" % "3.1.0"
)

enablePlugins(PlayScala)
enablePlugins(ScalikejdbcPlugin)

lazy val scalikejdbcVersion = scalikejdbc.ScalikejdbcBuildInfo.version
lazy val scalikejdbcPlayVersion = "2.8.0-scalikejdbc-3.5"
