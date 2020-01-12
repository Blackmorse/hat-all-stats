name := """web"""
organization := "com.blackmorse.hattrick"

resolvers += Resolver.mavenLocal

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.blackmorse.hattrick" % "api" % "0.0.3"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.blackmorse.hattrick.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.blackmorse.hattrick.binders._"
