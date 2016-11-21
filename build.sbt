import sbt.Keys._

import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

organization := "zalando"
scalaVersion := "2.11.8"


packageName in Docker := packageName.value
version in Docker := version.value
mappings in Docker += file("scm-source.json") -> "scm-source.json"
dockerRepository := Some("pierone.stups.zalan.do/octopus")
mappings in Universal += file("jolokia/jolokia-jvm-1.3.2-agent.jar") -> "jolokia/jolokia-jvm-1.3.2-agent.jar"
dockerCommands := Seq(
  Cmd("FROM", "registry.opensource.zalan.do/stups/openjdk:8u91-b14-1-22"),
  Cmd("RUN", "apt-get update && apt-get install -y software-properties-common"),
  Cmd("WORKDIR", "/opt/docker"),
  Cmd("ADD", "opt /opt"),
  Cmd("ADD", "scm-source.json /"),
  Cmd("RUN", """["chown", "-R", "998:998", "."]"""),
  Cmd("USER", "998")
)



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
    "org.scaldi" %% "scaldi-akka" % "0.5.8",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "2.2.0" % "test"

  )
}

dockerCommands ++= Seq(
  Cmd("EXPOSE", "9000"),
  Cmd("ENTRYPOINT", s"bin/${packageName.value} -Dconfig.resource=application-aws.conf $${jvm_conf} -J-server -J-javaagent:/opt/docker/jolokia/jolokia-jvm-1.3.2-agent.jar=host=*,port=8776"),
  ExecCmd("CMD")
)



enablePlugins(ScmSourcePlugin)
enablePlugins(JavaAppPackaging)



