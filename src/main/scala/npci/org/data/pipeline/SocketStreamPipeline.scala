package npci.org.data.pipeline

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.flink.api.scala._
import npci.org.data.constants.Constants
import npci.org.data.contract.FlinkStreamPipeline
import npci.org.data.models.Models.Person
import npci.org.data.transformers.{PersonTransformer, StringWordTransfomer}
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.core.fs.Path
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy
import org.apache.flink.streaming.api.functions.sink.filesystem.{BucketAssigner, StreamingFileSink}
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment



class SocketStreamPipeline   extends FlinkStreamPipeline[String,Iterable[String],Person] with java.io.Serializable {
  //def start(args:Array[String],env:StreamExecutionEnvironment ) = {
    def start(env:StreamExecutionEnvironment ) = {
      val args = Array("localhost","9999")
    if(args.length < 2){
      //System.err("argument not provided properly")
      System.err.println("ERROR: argument not provided")
      System.exit(-1)

    }
    val properties = new Properties
    properties.setProperty("hostName",args(0))
    properties.setProperty("port",args(1))



    env.getCheckpointConfig.setCheckpointStorage("file:///Users/sambhav.gupta/Documents/DECodePair/UsefulExampleTests/FlinkTry/FlinkAppTemplate/my-checkpoint-dir")

    //Source
    val source: DataStream[String] = getStreamingSource(env,properties )

    //Transformation
    val transormedStream: DataStream[Iterable[String]] = applyTransformation(env,source )

    //Sink
    val sink = getStreamingSink(env,transormedStream)

    //Side Effect
    val personStream = PersonTransformer.transform(transormedStream)

    personStream.addSink(sink)
    //val personStream = rawStream.flatMap{  w => w.split("\\s")}
    personStream print()

    env.execute()
  }

  ////override def getStreamingSink(streamExecutionEnvironment: StreamExecutionEnvironment): DataStreamSink[Person] = ???
  override def getStreamingSource(streamExecutionEnvironment: StreamExecutionEnvironment, props: Properties): DataStream[String] = {
    val rawStream  = streamExecutionEnvironment.socketTextStream(props.getProperty(Constants.HOSTNAME),props.getProperty(Constants.PORT).toInt)
    rawStream
  }

  override def applyTransformation(streamExecutionEnvironment: StreamExecutionEnvironment,stream:DataStream[String]): DataStream[Iterable[String]] = {
    StringWordTransfomer.transform(stream)
  }

  override def getStreamingSink(streamExecutionEnvironment: StreamExecutionEnvironment,stream:DataStream[Iterable[String]])= {

    val sink: StreamingFileSink[Person] = StreamingFileSink.forRowFormat(
      new Path("./filePath_target-with-chkpoint"),
      new SimpleStringEncoder[Person]("UTF-8"))
      //.withRollingPolicy()
      .withBucketAssigner(new EventTimeBucketAssigner())
      .withRollingPolicy(
        DefaultRollingPolicy.builder()
          .withRolloverInterval(TimeUnit.MINUTES.toMillis(2))
          .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
          .withMaxPartSize(1024 * 1024 * 1024)
          .build())
      .build()
    sink
  }

}

class EventTimeBucketAssigner extends BucketAssigner[Person, String] {
  override def getBucketId(element: Person, context: BucketAssigner.Context): String = {

    /// val partitionValue = new SimpleDateFormat("yyyyMMdd").format(new Date(date))
    import java.util.Random
    val rn = new Random
    val partitionValue = rn.nextInt(2) + 1
    "dt=" + partitionValue
  }

  override def getSerializer: SimpleVersionedSerializer[String] = {

    return SimpleVersionedStringSerializer.INSTANCE
  }
}