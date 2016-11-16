import sbt.Keys._

organization := "zalando"
scalaVersion := "2.11.8"


libraryDependencies ++= {
  val akkaVersion = "2.4.11"
  val playVersion = "2.4.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.github.seratch" %% "awscala" % "0.5.+",
    "com.typesafe.play" %% "play-json" % playVersion,
    "org.kairosdb" % "client" % "2.1.1",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "2.2.0" % "test"

  )
}



