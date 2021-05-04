name := """web"""
organization := "com.blackmorse.hattrick"

resolvers += Resolver.mavenLocal

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.5"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.blackmorse.hattrick" % "api" % "0.0.13"
libraryDependencies += "com.blackmorse.hattrick" % "hattrick-common" % "0.0.3"

libraryDependencies += jdbc
libraryDependencies += "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.2.3"

libraryDependencies += "ai.x" %% "play-json-extensions" % "0.42.0"


libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.4"
libraryDependencies += caffeine

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.blackmorse.hattrick.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.blackmorse.hattrick.binders._"
