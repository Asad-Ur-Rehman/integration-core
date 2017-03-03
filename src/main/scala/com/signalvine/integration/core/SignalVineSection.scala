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

case class SignalVineSection(programId: UUID[Program], url: String, token: String, secret: String , fields: Seq[Field])

object SignalVineSection {
  implicit val signalVineSectionWrites: Writes[SignalVineSection] = (
    (__ \ 'programId).write[UUID[Program]] and
    (__ \ 'url).write[String] and
    (__ \ 'token).write[String] and
    (__ \ 'secret).write[String] and
    (__ \ 'fields).write[Seq[Field]]
  ) (unlift(SignalVineSection.unapply))

  implicit val signalVineSectionReads: Reads[SignalVineSection] = (
    (__ \ 'programId).read[UUID[Program]] and
    (__ \ 'url).read[String] and
    (__ \ 'token).read[String] and
    (__ \ 'secret).read[String] and
    (__ \ 'fields).read[Seq[Field]]
  ) (SignalVineSection.apply _)
}
