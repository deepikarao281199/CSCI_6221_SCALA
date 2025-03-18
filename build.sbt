name := """weather-dashboard"""
organization := "com.weatherdashboard"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.weatherdashboard.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.weatherdashboard.binders._"
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.postgresql" % "postgresql" % "42.5.1",
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

libraryDependencies ++= Seq(
  // Existing dependencies...
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.postgresql" % "postgresql" % "42.5.1"
)


