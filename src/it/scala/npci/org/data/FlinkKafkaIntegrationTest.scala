package npci.org.data



import java.util
import java.util.concurrent.{Callable, TimeUnit}

import npci.org.data.pipeline.{KafkaStreamingPipeline}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.file.src.FileSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen}
import org.scalatest.matchers.must.Matchers
import org.slf4j.LoggerFactory
import org.apache.flink.connector.file.src.reader.TextLineFormat
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.scala._
import org.scalatest.flatspec.AnyFlatSpec


import java.util.concurrent.Executors

class FlinkKafkaIntegrationTest  extends AnyFlatSpec with GivenWhenThen  with BeforeAndAfterAll with  Matchers {

  var env : StreamExecutionEnvironment = _
  private val logger = LoggerFactory.getLogger("FlinkKafkaIntegrationTest")
 var records:Seq[String] = _
  override def beforeAll() {

    logger.info("Starting Kafka-Flink Integration Test...")  // start up your web server or whatever
    env = StreamExecutionEnvironment.createLocalEnvironment();
  }

  override def afterAll() {
    println("Completed Kafka-flink Integration Test!!")  // shut down the web server
  }

  "A Flink Pipeline " should "process records consumed from Kafka Topic" in {

    Given("A kafka Server Topic and Running Flink Pipeline ")
    import sys.process._
      val topicScript = "scripts/it/createTopic.sh" !! ;
      val result = "scripts/it/produce_kafka_record.sh" !! ;
      logger.info("SENT THE DATA ON KAFKA")
      val kafkaStreamApp = new KafkaStreamingPipeline()
      //Task Thread to run the Flink App as separate thread and kill manually later because of its streaming nature
      val task1  = new Callable[Unit]() {
        override def call(): Unit = try {
          kafkaStreamApp.main(Array[String]())
        } catch {
          case ex: InterruptedException =>
            throw new IllegalStateException(ex)
        }
      }
      val executorService = Executors.newFixedThreadPool(2)
      executorService.invokeAll(java.util.Arrays.asList(task1), 40, TimeUnit.SECONDS) // Timeout of 10 minutes.
      executorService.shutdown();

    When("a record is produced to the kafka topic ")
     // import sys.process._
     // val result = "scripts/it/produce_kafka_record.sh" !! ;
      //logger.info("SENT THE DATA ON KAFKA")

    Then("Processed output should have two records written to the storage sink")
      Thread.sleep(4000)
      val source  = FileSource.forRecordStreamFormat(new TextLineFormat(),new Path("./filePath_kafka_store"))
        .build()
      val stream =
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "file-source");
      records = stream.executeAndCollect().toSeq
      records.foreach( record => {
        logger.info("printing records: "+record)
      })

    And("One Record should be generated in FileSytem")
      logger.info("The record Count processed :"+records.size)
      assert(records.size === 1,"Records Not Equal to 1")

    logger.info("Integration Test Ended")
  }

}
