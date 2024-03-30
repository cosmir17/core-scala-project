name := "core-scala-project"

version := "1.0"

scalaVersion := "3.4.0"

trapExit := false

libraryDependencies ++= Seq(
    "org.typelevel"      %% "cats-core"          % "2.10.0",
    "org.typelevel"      %% "squants"            % "1.8.3",
    "io.github.iltotore" %% "iron"               % "2.5.0",
    "org.scalatest"      %% "scalatest"          % "3.2.18"   % Test,
    "org.scalatest"      %% "scalatest-funsuite" % "3.2.18"   % Test,
    "io.cucumber"        %% "cucumber-scala"     % "8.21.1"   % Test,
    "io.cucumber"        %  "cucumber-junit"     % "7.16.1"   % Test,
    "org.junit.jupiter"  %  "junit-jupiter-api"  % "5.10.2"   % Test,
    //  "com.github.sbt"     %  "junit-interface"    % "0.13.3"   % Test ** Cucumber tests disabled.

    //coding-gym teaching dependencies
    "org.typelevel"      %% "cats-effect"        % "3.5.4",
    "org.typelevel"      %% "log4cats-core"      % "2.6.0",
    "org.typelevel"      %% "log4cats-slf4j"     % "2.6.0",
    "ch.qos.logback"     % "logback-classic"     % "1.5.3"
)