

name := "hattid-scala"
trapExit := false

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / organization := "com.blackmorse.hattrick"

lazy val webDependencies = Seq(
  guice,
  jdbc,
  caffeine,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "com.blackmorse.hattrick" % "api" % "0.0.13",
  "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.2.3",
  "ai.x" %% "play-json-extensions" % "0.42.0",
  "org.playframework.anorm" %% "anorm" % "2.6.4",
)

lazy val webSettings = Seq(
  name := "web",
  resolvers += Resolver.mavenLocal,
  version := "1.0-SNAPSHOT",
  libraryDependencies ++= webDependencies,
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
)


val akkaVersion = "2.6.14"
val akkaHttpVersion = "10.1.14"
lazy val akkaLoaderDependencies = Seq(
  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "io.spray" %% "spray-json" % "1.3.6",
  "com.crobox.clickhouse" %% "client" % "0.9.19"
)

lazy val akkaLoaderSetting = Seq(
  name := "akka-loader",
  version := "0.1",
  libraryDependencies ++= akkaLoaderDependencies,
    trapExit := false
)

lazy val chppSettings = Seq(
  libraryDependencies ++= Seq(
    "commons-codec" % "commons-codec" % "1.15",
    "com.lucidchart" %% "xtract" % "2.2.1",
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  )
)

lazy val scalaCommon = (project in file("scala-common"))

lazy val chpp = (project in file("chpp"))
  .settings(chppSettings)

lazy val akkaLoader = (project in file("akka-loader"))
  .dependsOn(scalaCommon)
  .dependsOn(chpp)
  .settings(akkaLoaderSetting)

lazy val web = (project in file("web"))
  .dependsOn(scalaCommon)
  .dependsOn(chpp)
  .settings(webSettings)
  .enablePlugins(PlayScala)


