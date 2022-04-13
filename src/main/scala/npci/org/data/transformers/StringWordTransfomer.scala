package npci.org.data.transformers

import npci.org.data.contract.Transformations
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.DataStream

object StringWordTransfomer extends Transformations[String,Iterable[String]] {
  override def transform(inputStream: DataStream[String]): DataStream[Iterable[String]] = {

    inputStream.map( ele => ele.split(",").toIterable)
  }
}
