package npci.org.data.transformers

import java.util

import org.apache.flink.api.java.{DataSet, ExecutionEnvironment}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.planner.utils.Logging
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.apache.flink.streaming.api.datastream.DataStreamUtils
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.slf4j.LoggerFactory
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import java.util
import java.util.concurrent.locks.ReentrantReadWriteLock

import npci.org.data.util.sink.EventsIterableStringCollectionSink

import scala.collection.JavaConversions._

class StringWordTransformerTest extends org.scalatest.funsuite.AnyFunSuite with Logging with
BeforeAndAfterAll with  Matchers {

  var env : StreamExecutionEnvironment = _
  private val logger = LoggerFactory.getLogger("StringWordTransformerTest")

  override def beforeAll() {
    logger.info("Starting test....")  // start up your web server or whatever
    env = StreamExecutionEnvironment.createLocalEnvironment();
  }

  override def afterAll() {
    logger.info("Completing Test!!")  // shut down the web server
  }


  test("Test PersonTransformer with Sequence of CSV to be converted to Sequence of String") {

    val expectedResult: Seq[Seq[String]] = Seq(Seq("1","john"),Seq("2","wick"))
    val inputDataStringStream: DataStream[String] = env.fromCollection(Seq("1,john", "2,wick"))
    val outputDataIterableStream: DataStream[Iterable[String]] = StringWordTransfomer.transform(inputDataStringStream)
    val dummySink = new EventsIterableStringCollectionSink()
    outputDataIterableStream.addSink(dummySink)

    env.execute()
    val actualOutput: util.Map[String, Iterable[String]] = dummySink.getValues
    val actualResults = actualOutput.values().toList

    assert(actualOutput.size() === 2,"The transformed records count is not correct" )
    actualResults should contain theSameElementsAs( expectedResult)

    //For Understanding
    for (entry <- actualOutput.entrySet) {
      println("Key = " + entry.getKey + ", Value = " + entry.getValue)
    }

  }
}


