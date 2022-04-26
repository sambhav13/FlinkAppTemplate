
name := "FlinkAppTemplate"

version := "0.1"

scalaVersion := "2.11.7"



val flinkVersion = "1.13.0"

val scalaDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.11" % "it,test" ,
  "org.scalatest" %% "scalatest-funsuite" % "3.2.11" % "it,test",
  "org.scalatest" %% "scalatest-flatspec" % "3.2.11" % "it,test",
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % "it,test",
    // https://mvnrepository.com/artifact/org.apache.flink/flink-test-utils
  "org.apache.flink" %% "flink-test-utils" % "1.13.0" % "it,test",
  "org.apache.flink" %% "flink-clients" % flinkVersion % "it,test",
  "org.apache.flink" %% "flink-scala" % flinkVersion  % "it,test",
  "org.apache.flink" %% "flink-scala" % flinkVersion % "it,test",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "it,test"

)



val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-clients" % flinkVersion,
  "org.apache.flink" %% "flink-scala" % flinkVersion ,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion ,
  "org.apache.flink" %% "flink-table-planner-blink" % flinkVersion % "it,test",
  "org.apache.flink" %% "flink-connector-kafka" % "1.13.0" ,
  //  "org.apache.avro" %% "avro" % "1.8.2",
  "org.apache.flink" %% "flink-table-api-scala" % "1.13.0",
  "org.apache.flink" %% "flink-table-planner" % "1.13.0",
  "org.apache.flink" %% "flink-parquet" % "1.13.0",
  "org.apache.hudi" %% "hudi-flink" % "0.10.1",
  "org.apache.kafka" % "kafka-clients" % "0.11.0.0",
  "org.apache.flink" %  "flink-connector-files" % "1.13.0"

)


val loggingDependencies = Seq(
  "org.slf4j" % "slf4j-simple" % "1.7.36",
  "org.slf4j" % "slf4j-api" % "1.7.36",
  "ch.qos.logback" % "logback-core" % "1.0.9",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "org.slf4j" % "slf4j-api" % "1.7.36"
)


lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .configs(Test)
  .settings(
    Defaults.itSettings,
    libraryDependencies ++= flinkDependencies,
    libraryDependencies ++= scalaDependencies,
    libraryDependencies ++= loggingDependencies,
    assembly / mainClass := Some("npci.org.data.StreamingPipeline")
  )


lazy val utils = (project in file("utils"))
  .settings(
    assembly / assemblyJarName := "FlinkAppTemplate-Assembly.jar"
    // more settings here ...
  )
import sys.process._

//Running a custom task
lazy val execScript = taskKey[Unit]("Execute the shell script")
execScript := {
  println("starting Integration Test....")
  "./scripts/run.scala" !!

}


import scala.sys.process.Process


lazy val startUpTask = taskKey[Unit]("starting the docker containers")

startUpTask := {
  Process("./scripts/it/start.sh") !!
}

lazy val tearDownTask = taskKey[Unit]("stopping the docker containers")

tearDownTask := {
  Process("./scripts/it/end.sh") !!
}

commands += Command.command("itTest") { state =>
  "tearDownTask" ::
    "startUpTask" ::
    "it:test" ::
    "tearDownTask" ::
    state
}

coverageEnabled := true


lazy val rClassPath =  taskKey[Unit]("")

rClassPath := {

        (fullClasspath in Runtime).value.files.map(_.toString()).foreach(println)
}

lazy val itClassPath =  taskKey[Unit]("")

itClassPath := {

  (fullClasspath in IntegrationTest).value.files.map(_.toString()).foreach(println)
}


assembly / mainClass := Some("npci.org.data.StreamingPipeline")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
//assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = false)
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
