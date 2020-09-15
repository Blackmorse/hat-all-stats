name := """web"""
organization := "com.blackmorse.hattrick"

resolvers += Resolver.mavenLocal

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.12"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.blackmorse.hattrick" % "api" % "0.0.13"
libraryDependencies += "com.blackmorse.hattrick" % "hattrick-common" % "0.0.3"

libraryDependencies += jdbc
libraryDependencies += "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.2.3"


libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.2"
libraryDependencies += caffeine

libraryDependencies += "io.swagger" %% "swagger-play2" % "1.7.1"
libraryDependencies += "org.webjars" % "swagger-ui" % "2.2.0"
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.blackmorse.hattrick.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.blackmorse.hattrick.binders._"
