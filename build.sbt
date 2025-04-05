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

  // Use the full Play WS client
  "org.playframework" %% "play-ahc-ws" % "3.0.2",

  // Database
  "org.postgresql" % "postgresql" % "42.6.0",

  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)


// Fix for version conflicts
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always