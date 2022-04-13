package npci.org.data

import npci.org.data.pipeline.SocketStreamPipeline
import org.apache.flink.table.descriptors.Kafka

object StreamingPipeline{
  def main(args:Array[String]) = {
    println("Streaming Pipeline started....")


    // Creating and startin the Pipeline
    val socketStreamPipeline = new SocketStreamPipeline()
    socketStreamPipeline.start(args)
  }
}

