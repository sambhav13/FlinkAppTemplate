package npci.org.data.util.sink

import java.util
import java.util.concurrent.locks.ReentrantReadWriteLock

import npci.org.data.models.Models.Person
import org.apache.flink.streaming.api.functions.sink.SinkFunction

object EventsPersonCollectionSink {
  private val readWriteLock = new ReentrantReadWriteLock
  private var values =  new util.HashMap[String,Person]()
}

class EventsPersonCollectionSink extends SinkFunction[Person] {
  override final def invoke(in: Person, context: SinkFunction.Context): Unit = {
    EventsPersonCollectionSink.readWriteLock.writeLock.lock()
    try {
      EventsPersonCollectionSink.values.put(in.id,in)
    }
    finally {
      EventsPersonCollectionSink.readWriteLock.writeLock.unlock()
    }
  }

  def getValues: util.Map[String,Person] = {
    EventsPersonCollectionSink.readWriteLock.readLock.lock()
    try EventsPersonCollectionSink.values
    finally EventsPersonCollectionSink.readWriteLock.readLock.unlock()
  }
}