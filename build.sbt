
name := "datest"

version := "0.1"

scalaVersion := "2.12.7"

import DockerBuild._

lazy val akkaHttpVersion = "10.1.4"
lazy val akkaVersion    = "2.5.16"

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion    := "2.12.6"
    )),
    name := "hello-world",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test
    )
  )

lazy val mongoDBDriver  = "org.mongodb.scala" %% "mongo-scala-driver" % "2.2.1"
lazy val mongoScalaBson = "org.mongodb.scala" %% "mongo-scala-bson"   % "2.2.1"
lazy val mongoDBCodecs  = "ch.rasc"           %  "bsoncodec"          % "1.0.1"
lazy val cats = "org.typelevel" %% "cats-core" % "1.1.0"

libraryDependencies ++= Seq(mongoDBDriver, mongoScalaBson, mongoDBCodecs, cats)

mainClass in Compile := Some("com.example.QuickstartServer")