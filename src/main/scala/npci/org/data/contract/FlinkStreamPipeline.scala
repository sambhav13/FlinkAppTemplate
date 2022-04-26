package npci.org.data.contract

import java.util.Properties

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

trait FlinkStreamPipeline[I,T,O] {

  def getStreamingSource(streamExecutionEnvironment:StreamExecutionEnvironment, props:Properties): DataStream[I]

  def applyTransformation(streamExecutionEnvironment:StreamExecutionEnvironment,stream:DataStream[I]): DataStream[T]

  def getStreamingSink(streamExecutionEnvironment:StreamExecutionEnvironment,stream:DataStream[T]):RichSinkFunction[O]
}
