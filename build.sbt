

name := "hattid-scala"
trapExit := false

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / organization := "com.blackmorse.hattrick"

val clickhouseVersion = "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.2.3"
val anormVersion = "org.playframework.anorm" %% "anorm" % "2.6.4"

lazy val webDependencies = Seq(
  guice,
  jdbc,
  caffeine,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  clickhouseVersion,
  "ai.x" %% "play-json-extensions" % "0.42.0",
  anormVersion
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

val guiceVersion = "5.0.1"
val akkaVersion = "2.6.14"
val akkaHttpVersion = "10.1.14"
lazy val akkaLoaderDependencies = Seq(
  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.4.12",
  "io.spray" %% "spray-json" % "1.3.6",
  "com.crobox.clickhouse" %% "client" % "0.9.19",
  "com.google.inject" % "guice" % guiceVersion,
  "com.google.inject.extensions" % "guice-assistedinject" % guiceVersion,
  "org.rogach" %% "scallop" % "5.0.1"
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
    "com.typesafe.akka" %% "akka-stream" % akkaVersion //TODO  akka-streams instead of just akka-actor, because of strange errors with missing classes (check that it's true oO)
  )
)

lazy val scalaCommonSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion //TODO  akka-streams instead of just akka-actor, because of strange errors with missing classes (check that it's true oO)
  )
)

lazy val testSettings = Seq(
  libraryDependencies ++= Seq(
    clickhouseVersion,
    anormVersion
  )
)

lazy val scalaCommon = (project in file("scala-common"))
  .settings(scalaCommonSettings)

lazy val chpp = (project in file("chpp"))
  .settings(chppSettings)

lazy val sqlBuilder = (project in file("sqlBuilder"))
  .settings(Seq(
    libraryDependencies ++= Seq(
      anormVersion
    )
  ))

lazy val akkaLoader = (project in file("akka-loader"))
  .dependsOn(scalaCommon)
  .dependsOn(chpp)
  .settings(akkaLoaderSetting)

lazy val web = (project in file("web"))
  .dependsOn(scalaCommon)
  .dependsOn(chpp)
  .dependsOn(sqlBuilder)
  .settings(webSettings)
  .enablePlugins(PlayScala)

lazy val hattrickTests = (project in file("hattrick-tests"))
  .dependsOn(chpp)
  .dependsOn(scalaCommon)
  .dependsOn(sqlBuilder)
  .settings(testSettings)


