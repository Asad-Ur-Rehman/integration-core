package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.mutable.Specification

class ScheduleParserSpec extends Specification {
  "ScheduleParser" should {
    val scheduleParser: ScheduleParser = new ScheduleParserImpl
    "return the correct next datetime according to the schedule in Success" >> {
      scheduleParser.next("0 0 7 1/1 * ? *", DateTime.fromTime(2017, 3, 3, 11, 0, 0, 0)) mustEqual
        Scheduled(DateTime.fromTime(2017, 3, 4, 7, 0, 0, 0))

      scheduleParser.next("0 0/12 * 1/1 * ? *", DateTime.fromTime(2017, 3, 3, 11, 0, 0, 0)) mustEqual
        Scheduled(DateTime.fromTime(2017, 3, 3, 11, 12, 0, 0))

    }
    "return Failure for invalid expression" >> {
      scheduleParser.next("Invalid", DateTime.fromTime(2017, 3, 3, 11, 0, 0, 0)).isInstanceOf[InvalidSchedule]
    }
  }
}
