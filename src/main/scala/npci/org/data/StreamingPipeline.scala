package npci.org.data

import npci.org.data.pipeline.SocketStreamPipeline
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object StreamingPipeline{
  def main(args:Array[String]) = {
    println("Streaming Pipeline started....")


    // Creating and startin the Pipeline
    val socketStreamPipeline = new SocketStreamPipeline()

    //Create Streaming  Execution Env
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //socketStreamPipeline.start(args,env)
    socketStreamPipeline.start(env)
    println("Streaming Pipeline ended....")
  }
}

