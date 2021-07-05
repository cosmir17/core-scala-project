name := "core-scala-project"

version := "0.1"

scalaVersion := "2.13.6"

trapExit := false

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest"          % "3.2.9"   % Test,
    "org.scalatest" %% "scalatest-funsuite" % "3.2.9"   % Test,
    "io.cucumber"   %% "cucumber-scala"     % "7.0.0"   % Test,
    "io.cucumber"   %  "cucumber-junit"     % "6.10.4"  % Test,
    "junit"         %  "junit"              % "4.13.2"  % Test
)