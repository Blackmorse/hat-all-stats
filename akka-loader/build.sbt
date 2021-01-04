name := "akka-loader"

version := "0.1"

scalaVersion := "2.12.12"

val akkaVersion = "2.6.10"
val akkaHttpVersion = "10.2.2"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "commons-codec" % "commons-codec" % "1.15",
  "com.lucidchart" %% "xtract" % "2.2.1",
  "com.blackmorse.hattrick" % "hattrick-common" % "0.0.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",

)