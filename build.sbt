name := """weather-dashboard"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Update to Scala 2.13.15
scalaVersion := "2.13.15"

libraryDependencies ++= Seq(
  guice,
  // Play Framework dependencies
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",

  // Use the Play WS standalone client
  "com.typesafe.play" %% "play-ws-standalone" % "2.1.10",
  "com.typesafe.play" %% "play-ws-standalone-json" % "2.1.10",
  "com.typesafe.play" %% "play-ws-standalone-xml" % "2.1.10",

  // Database
  "org.postgresql" % "postgresql" % "42.6.0",

  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)


// Fix for version conflicts
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always