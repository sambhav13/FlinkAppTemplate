package npci.org.data.transformers

import java.util

import npci.org.data.models.Models.Person
import npci.org.data.util.sink.{EventsIterableStringCollectionSink, EventsPersonCollectionSink}
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.util.Logging
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._

import scala.collection.JavaConversions._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

class PersonTransformerTest extends org.scalatest.funsuite.AnyFunSuite with Logging
  with BeforeAndAfterAll with  Matchers{

  var env : StreamExecutionEnvironment = _


  override def beforeAll() {
    println("Before!")  // start up your web server or whatever
    env = StreamExecutionEnvironment.createLocalEnvironment()
  }

  override def afterAll() {
    println("After!")  // shut down the web server
  }


  test("Test PersonTransformer for ") {


    val expectedResult: Seq[Person] = Seq(Person("1","john"),Person("2","wick"))
    val inputDataStringStream: DataStream[Iterable[String]] = env.fromCollection(Seq(Seq("1","john").toIterable,Seq("2","wick").toIterable))
    //PersonTransformer.transform()

    val outputDataPersonStream: DataStream[Person] = PersonTransformer.transform(inputDataStringStream)
    val dummySink = new EventsPersonCollectionSink()
    outputDataPersonStream.addSink(dummySink)

    env.execute()
    val actualOutput: util.Map[String, Person] = dummySink.getValues
    val actualResults: util.Collection[Person] = actualOutput.values()

    assert(actualOutput.size() === 2,"The transformed records count is not correct" )
    actualResults should contain theSameElementsAs( expectedResult)
    import scala.collection.JavaConverters._
    //For Understanding
    actualResults.asScala.seq.foreach{ ele:Person => {
      println("Person Id: {0) ,  Person Name: {1} " , ele.id, ele.name)
    }}



  }
}
