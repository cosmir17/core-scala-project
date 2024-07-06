ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "father-picture-report-producer"
  )

libraryDependencies ++= Seq(
  "org.apache.poi" % "poi" % "5.3.0",
  "org.apache.poi" % "poi-ooxml" % "5.3.0",
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.23.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.23.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.23.1",

  "org.scalatest" %% "scalatest" % "3.2.18" % "test",
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % "test"
)

// Use the ScalaTest framework
testFrameworks += new TestFramework("org.scalatest.tools.Framework")

// Compile settings
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

//assembly / mainClass := Some("ImageDocGeneratorInKorean")
assembly / mainClass := Some("ImageDocGenerator")
//assembly / assemblyJarName := "ImageDocGeneratorInKorean.jar"
assembly / assemblyJarName := "ImageDocGenerator.jar"

// Add these merge strategies
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  case "module-info.class" => MergeStrategy.discard
  case x if x.endsWith("/module-info.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

// Add the following line to ensure test sources are recognised
Test / unmanagedSourceDirectories += baseDirectory.value / "src" / "test" / "scala"