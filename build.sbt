name := "hattid-scala"
trapExit := false

ThisBuild / scalaVersion := "3.7.4"
ThisBuild / organization := "com.blackmorse.hattrick"
val clickhouseVersion = "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.2.3"
val anormVersion = "org.playframework.anorm" %% "anorm" % "2.8.1"
val zioVersion = "2.1.21"
val tranzactIOVersion = "5.6.0"
val zioConfigVersion = "4.0.5"

lazy val webDependencies = Seq(
  guice,
  jdbc,
  caffeine,
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-cache" % "0.2.5",
  "dev.zio" %% "zio-prelude" % "1.0.0-RC41",
  "dev.zio" %% "zio-concurrent" % zioVersion,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  clickhouseVersion,
  "io.github.gaelrenoux" %% "tranzactio-anorm" % tranzactIOVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-http" % "3.5.1",
  "dev.zio" %% "zio-json" % "0.7.44"
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
  },
  Test / parallelExecution := false
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
  )
)

lazy val scalaCommonSettings = Seq()

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

lazy val webZio = (project in file("web-zio"))
  .dependsOn(scalaCommon)
  .dependsOn(chpp)
  .dependsOn(sqlBuilder)
  .settings(Seq(
    Compile / scalaSource := baseDirectory.value / "src" / "main" / "scala",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-cache" % "0.2.5",
      "dev.zio" %% "zio-prelude" % "1.0.0-RC41",
      "dev.zio" %% "zio-concurrent" % zioVersion,
      clickhouseVersion,
      "io.github.gaelrenoux" %% "tranzactio-anorm" % tranzactIOVersion,
      "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
      "dev.zio" %% "zio-config" % zioConfigVersion,
      "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio" %% "zio-http" % "3.5.1",
      "dev.zio" %% "zio-json" % "0.7.44",
      "dev.zio" %% "zio-test"          % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt"      % zioVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
  ))

