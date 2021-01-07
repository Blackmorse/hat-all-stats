package service

import java.util.Date
import java.util.concurrent.atomic.AtomicLong

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestCounterService @Inject()() {
  val startTime = new Date()
  val hoRequestsCounter = new AtomicLong(0L)

  def hoRequest = hoRequestsCounter.incrementAndGet()

  def getHoRequests = (hoRequestsCounter.get(), startTime.toString)
}
