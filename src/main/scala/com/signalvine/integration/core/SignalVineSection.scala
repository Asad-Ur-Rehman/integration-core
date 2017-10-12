package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed case class Field(name: String, `type`: String)

object Field {
  implicit val fieldWrites: Writes[Field] = (
    (__ \ 'name).write[String] and
    (__ \ 'type).write[String]
  ) (unlift(Field.unapply))

  implicit val fieldReads: Reads[Field] = (
    (__ \ 'name).read[String] and
    (__ \ 'type).read[String]
  ) (Field.apply _)
}

sealed trait Program

case class SignalVineSection(programId: UUID[Program], token: String, secret: String , fields: Seq[Field])

object SignalVineSection {

  val signalVineSectionWritesWithTokenSecret: Writes[SignalVineSection] = new Writes[SignalVineSection] {
    override def writes(o: SignalVineSection): JsValue = Json.obj(
      "programId" -> o.programId,
      "token" -> o.token,
      "secret" -> o.secret,
      "fields" -> o.fields
    )
  }

  implicit  val signalVineSectionWrites: Writes[SignalVineSection] = new Writes[SignalVineSection] {
    override def writes(o: SignalVineSection): JsValue = Json.obj(
      "programId" -> o.programId,
      "fields" -> o.fields
    )
  }


    implicit val signalVineSectionReads: Reads[SignalVineSection] = (
    (__ \ 'programId).read[UUID[Program]] and
    (__ \ 'token).read[String] and
    (__ \ 'secret).read[String] and
    (__ \ 'fields).read[Seq[Field]]
  ) (SignalVineSection.apply _)
}
