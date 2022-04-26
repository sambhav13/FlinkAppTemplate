# FLINK APP TEMPLATE
A template to write and structure Flink Applications. This template comprises of a basic flow to create Flink Application
with both Datastream APIs. The Project structure is designed in a way to enable reuse of Interfaces to write 
custom tranformation logic by implementing the Template interfaces . 


   
## Project Structure
The Project Template is sbt project consisting of Dependencies and unit tests and integration test directories configured in
it . The build.sbt has been configured to run the unit tests and integration tests along with other build in tasks

Source Code - src/main/scala

Unit Test - src/test/scala

Integration Test - src/it/scala

scripts - scripts/.... .sh

docker compose files -  docker-compose.yaml... etc.
## Specifications and Requirements

- jdk-1.8
- scala-2.11.7
- sbt 1.1.x
- docker-compose 1.29.2

## Commands
Unit Test
- sbt test

Integration Test
- sbt itTest

Build
- sbt compile

Run
Start a socket server and Enter data once Flink App is running
- nc -lk 9999
- Enter (1,john) Press [Enter]
- Enter (2,rambo) Press [Enter]

Start a sbt shell
- sbt

Type the following command

-  run localhost 9999
 
  OR
  
Directly run this command from terminal
- sbt "run localhost 9999"
  
Assembly/Fat Jar
- sbt assembly


   
## Summary
Use this Template to refer for basic project structure and how to 
write Flink App and relevant test cases using scala test test styles
(flatspec and  FunSuite). It also has a sample to show how to create a custom task
using TaskKey.


