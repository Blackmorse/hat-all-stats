package com.blackmorse.hattid.web.service

import java.util.Date
import java.util.concurrent.atomic.AtomicLong

class RequestCounterService {
  val startTime = new Date()
  val hoRequestsCounter = new AtomicLong(0L)

  def hoRequest: Long = hoRequestsCounter.incrementAndGet()

  def getHoRequests: (Long, String) = (hoRequestsCounter.get(), startTime.toString)
}
