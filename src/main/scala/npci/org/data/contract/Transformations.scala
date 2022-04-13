package npci.org.data.contract

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.DataStream
trait Transformations[I,O] {

  def transform(inputStream:DataStream[I]) :DataStream[O]
}
