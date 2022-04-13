package npci.org.data.transformers

import npci.org.data.contract.Transformations
import npci.org.data.models.Models.Person
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.api.scala._


object PersonTransformer extends Transformations[Iterable[String],Person]{
  override def transform(inputStream: DataStream[Iterable[String]]): DataStream[Person] = {

    val outputStream: DataStream[Person] = inputStream
      .map(p => {
        val columns = p.toSeq
        Person(columns(0),columns(1))
      })
    outputStream
  }
}
