
name := "FlinkAppTemplate"

version := "0.1"

scalaVersion := "2.11.7"



val scalaDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.11" % "it,test" ,
  "org.scalatest" %% "scalatest-funsuite" % "3.2.11" % "it,test",
  "org.scalatest" %% "scalatest-flatspec" % "3.2.11" % "it,test"
)



val flinkVersion = "1.13.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-clients" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-planner-blink" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-connector-kafka" % "1.13.0" % "provided",
  //  "org.apache.avro" %% "avro" % "1.8.2",
  "org.apache.flink" %% "flink-table-api-scala" % "1.13.0",
  "org.apache.flink" %% "flink-table-planner" % "1.13.0" % "provided",
  "org.apache.flink" %% "flink-parquet" % "1.13.0",
  "org.apache.hudi" %% "hudi-flink" % "0.10.1",
  "org.slf4j" % "slf4j-simple" % "1.7.36",
  "org.slf4j" % "slf4j-api" % "1.7.36"
)


lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    libraryDependencies ++= flinkDependencies,
    libraryDependencies ++= scalaDependencies
  )


import sys.process._

//Running a custom task
lazy val execScript = taskKey[Unit]("Execute the shell script")
execScript := {
  println("starting Integration Test....")
  "./scripts/run.scala" !!

}


(test in IntegrationTest) := ((test in IntegrationTest) dependsOn execScript).value





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
