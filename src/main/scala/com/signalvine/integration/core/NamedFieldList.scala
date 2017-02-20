package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class NamedFieldList(name: String, fields: Lists.FieldList)

object NamedFieldList {
  implicit val targetFieldWrite: Writes[NamedFieldList] = (
    (__ \ 'name).write[String] and
    (__ \ 'fields).write[Lists.FieldList]
  ) (unlift(NamedFieldList.unapply))

  implicit val authFieldRead: Reads[NamedFieldList] = (
    (__ \ 'name).read[String] and
    (__ \ 'fields).read[Lists.FieldList]
  ) (NamedFieldList.apply _)
}