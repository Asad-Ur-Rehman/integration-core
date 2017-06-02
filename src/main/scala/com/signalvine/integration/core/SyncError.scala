package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait IntegrationError {
  val message: String
  val details: Option[String]
}

sealed trait Participant

case class SyncError(
                      participantId: Option[UUID[Participant]],
                      colName: Option[String],
                      row: Option[Int],
                      column: Option[Int],
                      message: String,
                      details: Option[String]
                    ) extends IntegrationError

object SyncError {
  implicit val optionStringWrites: Writes[Option[String]] = Writes.optionWithNull[String]
  implicit val optionStringReads: Reads[Option[String]] = Reads.optionWithNull[String]
  implicit val optionIntWrites: Writes[Option[Int]] = Writes.optionWithNull[Int]
  implicit val optionIntReads: Reads[Option[Int]] = Reads.optionWithNull[Int]

  implicit val syncErrorReads: Reads[SyncError] =(
    (__ \ 'participantId).readNullable[UUID[Participant]] and
    (__ \ 'colname).readNullable[String] and
    (__ \ 'row).readNullable[Int] and
    (__ \ 'col).readNullable[Int] and
    (__ \ 'msg).read[String] and
    (__ \ 'details).readNullable[String]
  ) (SyncError.apply _)

  implicit val syncErrorWrites: Writes[SyncError] = (
    (__ \ 'participantId).writeNullable[UUID[Participant]] and
    (__ \ 'colname).writeNullable[String] and
    (__ \ 'row).writeNullable[Int] and
    (__ \ 'col).writeNullable[Int] and
    (__ \ 'msg).write[String] and
    (__ \ 'details).writeNullable[String]
  ) (unlift(SyncError.unapply))
}