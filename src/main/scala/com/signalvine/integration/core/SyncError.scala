package com.signalvine.integration.core

//import io.jvm.uuid._
import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait IntegrationError {
  val message: String
  val details: String
}

sealed trait Participant

case class SyncError(
                      participantId: UUID[Participant],
                      message: String,
                      details: String
                    ) extends IntegrationError

object SyncError {
  implicit val syncErrorReads: Reads[SyncError] =(
    (__ \ 'participantId).read[UUID[Participant]] and
    (__ \ 'message).read[String] and
    (__ \ 'details).read[String]
  ) (SyncError.apply _)

  implicit val syncErrorWrites: Writes[SyncError] = (
    (__ \ 'participantId).write[UUID[Participant]] and
    (__ \ 'message).write[String] and
    (__ \ 'details).write[String]
  ) (unlift(SyncError.unapply))
}