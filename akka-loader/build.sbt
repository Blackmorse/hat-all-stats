name := "akka-loader"

version := "0.1"

scalaVersion := "2.12.12"

val akkaVersion = "2.6.10"
val akkaHttpVersion = "10.2.2"

libraryDependencies ++= Seq(
  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "commons-codec" % "commons-codec" % "1.15",
  "com.lucidchart" %% "xtract" % "2.2.1"

)