package npci.org.data.pipeline

import java.io.StringWriter
import java.time.LocalTime
import java.util.Properties
import java.util.concurrent.TimeUnit

import npci.org.data.constants.Constants
import npci.org.data.contract.FlinkStreamPipeline
import npci.org.data.models.Models.Person
import npci.org.data.transformers.{PersonTransformer, StringWordTransfomer}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.{FileSystem, Path}
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.{DefaultRollingPolicy, OnCheckpointRollingPolicy}
import org.apache.flink.configuration.Configuration
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner
import org.slf4j.LoggerFactory

/**
 * Simple example on how to read with a Kafka consumer
 *
 * Note that the Kafka source is expecting the following parameters to be set
 *  - "bootstrap.servers" (comma separated list of kafka brokers)
 *  - "zookeeper.connect" (comma separated list of zookeeper servers)
 *  - "group.id" the id of the consumer group
 *  - "topic" the name of the topic to read data from.
 *
 * You can pass these required parameters using "--bootstrap.servers host:port,host1:port1 --zookeeper.connect host:port --topic testTopic"
 *
 * This is a valid input example:
 * 		--topic test --bootstrap.servers localhost:9092 --zookeeper.connect localhost:2181 --group.id myGroup
 *
 *
 */
class KafkaStreamingPipeline extends FlinkStreamPipeline[String,Person,Person] with java.io.Serializable {


  private val logger = LoggerFactory.getLogger("KafkaStreamingPipeline")
    def main(args:Array[String]) = {

      val environment = StreamExecutionEnvironment.getExecutionEnvironment
      val env = environment

      env.setParallelism(1)

       env.enableCheckpointing(10000)

      val config = new Configuration()
      env.getCheckpointConfig.setCheckpointStorage("file:///Users/sambhav.gupta/Documents/DECodePair/UsefulExampleTests/FlinkTry/FlinkAppTemplate/kafka/my-checkpoint-dir")
      val properties = new Properties
      //Source
      val source: DataStream[String] = getStreamingSource(env,properties )

      //Transformation
      val transormedStream: DataStream[Person] = applyTransformation(env,source )

      //Sink
      val sink = getStreamingSink(env,transormedStream)

      transormedStream.addSink(sink)
      env.execute()
      println("!!!!!")
  }

  override def getStreamingSource(streamExecutionEnvironment: StreamExecutionEnvironment, props: Properties): DataStream[String] = {
    val source: KafkaSource[String] = KafkaSource.builder()
      .setBootstrapServers("localhost:29092")
      .setTopics("test")
      .setGroupId("my-group4"+LocalTime.now().getSecond)
      .setStartingOffsets(OffsetsInitializer.earliest())
      .setValueOnlyDeserializer(new SimpleStringSchema())
      .build();

    val rawStream: DataStream[String] =  streamExecutionEnvironment.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
    rawStream
  }

  override def applyTransformation(streamExecutionEnvironment: StreamExecutionEnvironment,stream:DataStream[String]): DataStream[Person] = {
    val iterableStream: DataStream[Iterable[String]] = StringWordTransfomer.transform(stream)
    val personStream = PersonTransformer.transform(iterableStream)
    personStream
  }

  override def getStreamingSink(streamExecutionEnvironment: StreamExecutionEnvironment,stream:DataStream[Person])= {

    val sink: StreamingFileSink[Person] = StreamingFileSink.forRowFormat(
      new Path("./filePath_kafka_store"),
      new SimpleStringEncoder[Person]("UTF-8"))
      .withBucketAssigner(new PersonBucketAssigner())
      .withRollingPolicy(
        DefaultRollingPolicy.builder()
          .withRolloverInterval(TimeUnit.SECONDS.toMillis(10))
          .withInactivityInterval(TimeUnit.SECONDS.toMillis(20))
          .withMaxPartSize(10 )
          .build())
      .build()
    sink
  }

}

class PersonBucketAssigner extends BucketAssigner[Person, String] {
  override def getBucketId(element: Person, context: BucketAssigner.Context): String = {

    import java.util.Random
    val rn = new Random
    val partitionValue = rn.nextInt(2) + 1
    "dt=" + partitionValue
  }

  override def getSerializer: SimpleVersionedSerializer[String] = {

    return SimpleVersionedStringSerializer.INSTANCE
  }
}
