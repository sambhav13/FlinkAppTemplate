package npci.org.data.util.sink

import java.util
import java.util.concurrent.locks.ReentrantReadWriteLock

import org.apache.flink.streaming.api.functions.sink.SinkFunction

object EventsIterableStringCollectionSink {
  private val readWriteLock = new ReentrantReadWriteLock
  private var values =  new util.HashMap[String,Iterable[String]]()
}

class EventsIterableStringCollectionSink extends SinkFunction[Iterable[String]] {
  override final def invoke(in: Iterable[String], context: SinkFunction.Context): Unit = {
    EventsIterableStringCollectionSink.readWriteLock.writeLock.lock()
    try {
      EventsIterableStringCollectionSink.values.put(in.toSeq(0),in.toSeq)
    }
    finally {
      EventsIterableStringCollectionSink.readWriteLock.writeLock.unlock()
    }
  }

  def getValues: util.Map[String,Iterable[String]] = {
    EventsIterableStringCollectionSink.readWriteLock.readLock.lock()
    try EventsIterableStringCollectionSink.values
    finally EventsIterableStringCollectionSink.readWriteLock.readLock.unlock()
  }
}