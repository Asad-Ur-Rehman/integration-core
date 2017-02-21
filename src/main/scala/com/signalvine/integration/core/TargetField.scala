package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class TargetField(name: String, typ: String, id: String)

object TargetField {

  implicit val targetFieldWrite: Writes[TargetField] = (
    (__ \ 'name).write[String] and
    (__ \ 'type).write[String] and
    (__ \ 'id).write[String]
  ) (unlift(TargetField.unapply))

  implicit val authFieldRead: Reads[TargetField] = (
    (__ \ 'name).read[String] and
    (__ \ 'type).read[String] and
    (__ \ 'id).read[String]
  ) (TargetField.apply _)
}
