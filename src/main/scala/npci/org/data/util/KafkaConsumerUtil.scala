package npci.org.data.util

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory

class KafkaConsumerUtil {

 private val logger = LoggerFactory.getLogger(classOf[KafkaConsumerUtil])

  val props:Properties = new Properties()
  props.put("group.id", "test-"+System.currentTimeMillis())
  props.put("bootstrap.servers","localhost:29092")
  props.put("key.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")
  props.put("auto.offset.reset","earliest")
  val consumer = new KafkaConsumer(props)



  def consume(topicLst:String) = {
    val topics = List(topicLst)

    logger.info("running kafka consumer")
    try {
      consumer.subscribe(topics.asJava)
      val noOfRecords = 2
      //      for (i <- 0 until noOfRecords){
      import scala.util.control.Breaks._
      breakable {
        while(true){
          val records = consumer.poll(4000)
          for (record <- records.asScala) {
            println("Topic: " + record.topic() +
              ",Key: " + record.key() +
              ",Value: " + record.value() +
              ", Offset: " + record.offset() +
              ", Partition: " + record.partition())

            logger.info("Topic: " + record.topic() +
              ",Key: " + record.key() +
              ",Value: " + record.value() +
              ", Offset: " + record.offset() +
              ", Partition: " + record.partition())
            break()
          }
        }
      }
      logger.info("record read successfully!!")
    }catch {
      case e: Exception => e.printStackTrace()
    } finally {
      consumer.close()
    }
  }

}