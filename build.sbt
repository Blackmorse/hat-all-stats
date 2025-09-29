name := "hattid-scala"
trapExit := false

ThisBuild / scalaVersion := "3.7.3"
ThisBuild / organization := "com.blackmorse.hattrick"

val clickhouseVersion = "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.2.3"
val anormVersion = "org.playframework.anorm" %% "anorm" % "2.8.1"
val zioVersion = "2.1.21"
val tranzactIOVersion = "5.6.0"

lazy val webDependencies = Seq(
  guice,
  jdbc,
  caffeine,
  "dev.zio" %% "zio-cache" % "0.2.5",
  "dev.zio" %% "zio-prelude" % "1.0.0-RC41",
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  clickhouseVersion,
  "io.github.gaelrenoux" %% "tranzactio-anorm" % tranzactIOVersion,
)

lazy val webSettings = Seq(
  name := "web",
  resolvers ++= Seq(
    Resolver.mavenLocal,
  ),
  version := "1.0-SNAPSHOT",
  libraryDependencies ++= webDependencies,
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
)

val guiceVersion = "5.0.1"
val pekkoVersion = "1.0.3"
val pekkoHttpVersion = "1.1.0"
lazy val akkaLoaderDependencies = Seq(
  "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
  "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "io.spray" %% "spray-json" % "1.3.6",
  ("com.crobox.clickhouse" %% "client" % "1.2.6"),
//  .exclude("org.apache.pekko", "pekko-stream")
//  .exclude("org.apache.pekko", "pekko-actor")
//  .exclude("org.apache.pekko", "pekko-http"),
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
    ("com.lucidchart" %% "xtract" % "2.3.0")
      .exclude("org.scala-lang.modules", "scala-xml_2.13")
      .exclude("org.scala-lang.modules", "scala-collection-compat_2.13")
      .cross(CrossVersion.for3Use2_13),
    "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
    "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
    "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
    "dev.zio" %% "zio" % zioVersion,
    "dev.zio" %% "zio-streams" % zioVersion
  )
)

lazy val scalaCommonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
    "org.apache.pekko" %% "pekko-stream" % pekkoVersion
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
