package com.signalvine.integration.core

import scala.language.implicitConversions

import anorm.{Column, ToStatement, TypeDoesNotMatch}
import org.joda.{time => joda}
import play.api.data.validation.ValidationError
import play.api.libs.json._

/**
  * A newtype wrapper around joda DateTime.
  */
final case class DateTime(toJoda: joda.DateTime) extends AnyVal {
  override def toString: String = DateTime.formatter.print(toJoda)
  def getUnixTimestamp: Long = toJoda.getMillis / 1000
  def truncateDate: DateTime = withJoda(_.withMillisOfDay(0))
  def toLocalDate: LocalDate = LocalDate(toJoda.toLocalDate)

  def getMillis: Long = toJoda.getMillis
  def getYear: Int = toJoda.getYear
  def getMonthOfYear: Int = toJoda.getMonthOfYear
  def getDayOfMonth: Int = toJoda.getDayOfMonth
  def getHourOfDay: Int = toJoda.getHourOfDay
  def getMinuteOfHour: Int = toJoda.getMinuteOfHour
  def getSecondOfMinute: Int = toJoda.getSecondOfMinute
  def getMillisOfSecond: Int = toJoda.getMillisOfSecond

  def plusYears(years: Int): DateTime = withJoda(_.plusYears(years))
  def minusYears(years: Int): DateTime = withJoda(_.minusYears(years))
  def plusMonths(months: Int): DateTime = withJoda(_.plusMonths(months))
  def minusMonths(months: Int): DateTime = withJoda(_.minusMonths(months))
  def plusDays(days: Int): DateTime = withJoda(_.plusDays(days))
  def minusDays(days: Int): DateTime = withJoda(_.minusDays(days))
  def plusHours(hours: Int): DateTime = withJoda(_.plusHours(hours))
  def minusHours(hours: Int): DateTime = withJoda(_.minusHours(hours))
  def plusMinutes(minutes: Int): DateTime = withJoda(_.plusMinutes(minutes))
  def minusMinutes(minutes: Int): DateTime = withJoda(_.minusMinutes(minutes))
  def plusSeconds(seconds: Int): DateTime = withJoda(_.plusSeconds(seconds))
  def minusSeconds(seconds: Int): DateTime = withJoda(_.minusSeconds(seconds))
  def plusMillis(millis: Int): DateTime = withJoda(_.plusMillis(millis))
  def minusMillis(millis: Int): DateTime = withJoda(_.minusMillis(millis))

  def isBefore(dt: DateTime): Boolean = toJoda.isBefore(dt.toJoda)
  def isAfter(dt: DateTime): Boolean = toJoda.isAfter(dt.toJoda)

  def format(s: String): String = joda.format.DateTimeFormat.forPattern(s).print(this.toJoda)

  def -(dt: DateTime): Period = Period(this, dt)

  def withJoda(f: joda.DateTime => joda.DateTime): DateTime = DateTime(f(toJoda))
}

object DateTime {
  def now(): DateTime = DateTime(joda.DateTime.now(DateTimeZone.UTC))
  def parse(s: String): Either[String, DateTime] = try {
    Right(unsafeParse(s))
  } catch {
    case _: Throwable => Left(s"Invalid datetime; expected 'YYYY-MM-DDTHH:mm:ss.SSSZ', got: $s")
  }
  def unsafeParse(s: String): DateTime = DateTime(formatter.parseDateTime(s).withZone(DateTimeZone.UTC))
  def fromTime(instant: Long) = DateTime(new joda.DateTime(instant, DateTimeZone.UTC))
  def fromTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int, ms: Int): DateTime = {
    DateTime(new joda.DateTime(year, month, day, hour, minute, second, ms, DateTimeZone.UTC))
  }
  def fromDate(year: Int, month: Int, day: Int): DateTime = {
    DateTime(new joda.DateTime(year, month, day, 0, 0, DateTimeZone.UTC))
  }

  // To ensure DateTime Chronology matches - http://stackoverflow.com/a/21004086/1313611
  val formatter = joda.format.ISODateTimeFormat.dateTime().withZoneUTC

  implicit val formatInstance: Format[DateTime] = Format(
    Reads {
      case JsString(s) => parse(s).fold(e => JsError(e.toString), JsSuccess(_))
      case _ => JsError(ValidationError("error.expected.datetime"))
    },
    Writes(dt => JsString(dt.toString))
  )

  implicit val orderingInstance: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  implicit val toStatementInstance: ToStatement[DateTime] = new ToStatement[DateTime] {
    override def set(s: java.sql.PreparedStatement, index: Int, dt: DateTime): Unit = {
      if (dt == null) s.setNull(index, java.sql.Types.TIMESTAMP)
      else s.setTimestamp(index, new java.sql.Timestamp(dt.getMillis))
    }
  }

  implicit val columnInstance: Column[DateTime] = Column.nonNull1 { (value, meta) =>
    value match {
      case d: java.util.Date => Right(DateTime.fromTime(d.getTime))
      case s : java.lang.String => parse(s).left.map(e => TypeDoesNotMatch(e))
      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value to DateTime"))
    }
  }
}

/**
  * A newtype wrapper around joda LocalDate
  */
final case class LocalDate(toJoda: joda.LocalDate) extends AnyVal {
  override def toString: String = toJoda.toString
  def toDate: java.util.Date = toJoda.toDate
  def toDateTime: DateTime = DateTime(toJoda.toDateTimeAtStartOfDay(DateTimeZone.UTC))

  def getYear: Int = toJoda.getYear
  def getMonthOfYear: Int = toJoda.getMonthOfYear
  def getDayOfMonth: Int = toJoda.getDayOfMonth

  def plusYears(years: Int): LocalDate = withJoda(_.plusYears(years))
  def minusYears(years: Int): LocalDate = withJoda(_.minusYears(years))
  def plusMonths(months: Int): LocalDate = withJoda(_.plusMonths(months))
  def minusMonths(months: Int): LocalDate = withJoda(_.minusMonths(months))
  def plusDays(days: Int): LocalDate = withJoda(_.plusDays(days))
  def minusDays(days: Int): LocalDate = withJoda(_.minusDays(days))

  def isBefore(ld: LocalDate): Boolean = toJoda.isBefore(ld.toJoda)
  def isAfter(ld: LocalDate): Boolean = toJoda.isAfter(ld.toJoda)

  def withJoda(f: joda.LocalDate => joda.LocalDate): LocalDate = LocalDate(f(toJoda))
}

object LocalDate {
  def now(): LocalDate = LocalDate(joda.LocalDate.now(DateTimeZone.UTC))
  def parse(s: String): Either[String, LocalDate] = try {
    Right(unsafeParse(s))
  } catch {
    case _: Throwable => Left(s"Invalid date; expected 'YYYY-MM-DD', got: $s")
  }

  def unsafeParse(s: String): LocalDate = LocalDate(joda.LocalDate.parse(s))
  def fromDateFields(date: java.util.Date): LocalDate = LocalDate(joda.LocalDate.fromDateFields(date))
  def fromDate(y: Int, m: Int, d: Int): LocalDate = LocalDate(new joda.LocalDate(y, m, d))

  implicit val formatInstance: Format[LocalDate] = Format(
    Reads {
      case JsString(s) => parse(s).fold(e => JsError(e.toString), JsSuccess(_))
      case _ => JsError(ValidationError("error.expected.date"))
    },
    Writes(ld => JsString(ld.toString))
  )

  implicit val orderingInstance: Ordering[LocalDate] = Ordering.fromLessThan(_ isBefore _)

  implicit val toStatementInstance: ToStatement[LocalDate] = new ToStatement[LocalDate] {
    override def set(s: java.sql.PreparedStatement, index: Int, ld: LocalDate): Unit = {
      if (ld == null) s.setNull(index, java.sql.Types.DATE)
      else s.setDate(index, new java.sql.Date(ld.toDate.getTime))
    }
  }

  implicit val columnInstance: Column[LocalDate] = Column.nonNull { (value, meta) =>
    value match {
      case d: java.sql.Date => Right(LocalDate.fromDateFields(d))
      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value to LocalDate"))
    }
  }
}

final case class LocalTime(toJoda: joda.LocalTime) extends AnyVal {
  override def toString: String = toJoda.toString
}

object LocalTime {
  def parse(s: String): Either[String, LocalTime] = try {
    Right(unsafeParse(s))
  } catch {
    case _: Throwable => Left(s"Invalid time; expected 'HH:MM:SS', got: $s")
  }

  def unsafeParse(s: String): LocalTime = LocalTime(joda.LocalTime.parse(s))

  def fromTime(h: Int, m: Int, s: Int, ms: Int): LocalTime = {
    LocalTime(new joda.LocalTime(h, m, s, ms))
  }
}

/**
  * A newtype wrapper around joda Period
  */
final case class Period(toJoda: joda.Period) extends AnyVal {
  override def toString: String = toJoda.toString
  def getMillis: Int = toJoda.getMillis
  def getYears: Int = toJoda.getYears
  def getMonths: Int = toJoda.getMonths
  def getDays: Int = toJoda.getDays
  def getHours: Int = toJoda.getHours
  def getMinutes: Int = toJoda.getMinutes
  def getSeconds: Int = toJoda.getSeconds
}

object Period {
  def apply(start: DateTime, end: DateTime): Period = {
    Period(new joda.Period(start.toJoda, end.toJoda))
  }

  def fromMillis(millis: Int): Period = Period(new joda.Period(millis))
}

/**
  * Provides the DateTimeZone methods so we can avoid importing joda altogether.
  */
object DateTimeZone {
  lazy val UTC: joda.DateTimeZone = joda.DateTimeZone.UTC
  def getDefault: joda.DateTimeZone = joda.DateTimeZone.getDefault
  def setDefault(zone: joda.DateTimeZone): Unit = joda.DateTimeZone.setDefault(zone)
  def forID(id: String): joda.DateTimeZone = joda.DateTimeZone.forID(id)
}

/** A newtype wrapper around joda DateTimeFormatterBuilder */
final class DateTimeFormatterBuilder(val toJoda: joda.format.DateTimeFormatterBuilder)
  extends AnyVal {

  def toFormatter: joda.format.DateTimeFormatter = toJoda.toFormatter

  def appendDate: DateTimeFormatterBuilder = {
    withJoda(_
      .appendYear(4, 4)
      .appendLiteral('-')
      .appendMonthOfYear(2)
      .appendLiteral('-')
      .appendDayOfMonth(2)
    )
  }

  def appendTimeNoMillis: DateTimeFormatterBuilder = {
    withJoda(_
      .appendHourOfDay(2)
      .appendLiteral(':')
      .appendMinuteOfHour(2)
      .appendLiteral(':')
      .appendSecondOfMinute(2)
    )
  }

  def appendTimeWithMillis: DateTimeFormatterBuilder = {
    appendTimeNoMillis.appendLiteral('.').withJoda(_.appendMillisOfSecond(3))
  }

  def appendLiteral(c: Char) = withJoda(_.appendLiteral(c))

  def appendLiteral(s: String) = withJoda(_.appendLiteral(s))

  def withJoda
  (f: joda.format.DateTimeFormatterBuilder => joda.format.DateTimeFormatterBuilder)
  : DateTimeFormatterBuilder
  = DateTimeFormatterBuilder.fromJoda(f(toJoda))
}

object DateTimeFormatterBuilder {
  def apply(): DateTimeFormatterBuilder = {
    fromJoda(new joda.format.DateTimeFormatterBuilder)
  }

  def fromJoda(b: joda.format.DateTimeFormatterBuilder): DateTimeFormatterBuilder = {
    new DateTimeFormatterBuilder(b)
  }
}