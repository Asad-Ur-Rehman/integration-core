package com.signalvine.integration.core

import java.time.{ZoneId, ZonedDateTime}

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import com.google.inject.ImplementedBy

import scala.util.{Failure, Success, Try}

sealed trait JobSchedule
case class Scheduled(runtime: DateTime) extends JobSchedule
case class InvalidSchedule(msg: String, ex: Throwable) extends JobSchedule

@ImplementedBy(classOf[ScheduleParserImpl])
trait ScheduleParser{
  def next(expr: String, time: DateTime): JobSchedule
}

class ScheduleParserImpl extends ScheduleParser {
  def next(expr: String, time: DateTime): JobSchedule = {
    Try({
      val parser: CronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
      val executionTime: ExecutionTime = ExecutionTime.forCron(parser.parse(expr))
      val zonedDateTime = executionTime.nextExecution(
        ZonedDateTime.of(
          time.getYear,
          time.getMonthOfYear,
          time.getDayOfMonth, time.getHourOfDay,
          time.getMinuteOfHour,
          time.getSecondOfMinute,
          0,
          ZoneId.of("UTC")
        )
      )
      DateTime.fromTime(
        zonedDateTime.getYear,
        zonedDateTime.getMonthValue,
        zonedDateTime.getDayOfMonth,
        zonedDateTime.getHour,
        zonedDateTime.getMinute,
        zonedDateTime.getSecond,
        0
      )
    }) match {
      case Success(dateTime) => Scheduled(dateTime)
      case Failure(e) =>  InvalidSchedule(s"Unable to parse cron schedule ${expr}", e)
    }
  }
}
