name := "crawler2"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.0",
  "org.mongodb" %% "casbah" % "3.1.1"
)