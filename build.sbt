name := "core-scala-project"

version := "0.1"

scalaVersion := "2.12.8"

trapExit := false

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "io.cucumber" %% "cucumber-scala" % "4.2.0" % "test",
    "io.cucumber" % "cucumber-junit" % "4.2.4" % "test",
    "junit" % "junit" % "4.12" % "test"
)