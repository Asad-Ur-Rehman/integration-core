package com.signalvine.integration.core

import play.api.libs.json._

trait TargetField {
  def getName: String
  def isSimple: Boolean
}

object TargetField {
  implicit val fieldFormat: Format[TargetField] = new Format[TargetField] {
    def writes(field: TargetField): JsValue = {
      field match {
        case b: SimpleField => Json.toJson[SimpleField](b)(SimpleField.simpleFieldFmt)
        case b: GroupField => Json.toJson[GroupField](b)(GroupField.groupFieldFmt)
      }
    }

    def reads(json: JsValue): JsResult[TargetField] = {
      json.validate[SimpleField](SimpleField.simpleFieldFmt) match {
        case _: JsError => json.validate[GroupField]
        case s: JsSuccess[SimpleField] => s
      }
    }
  }
}

case class SimpleField(name: String, map: String, `type`: String) extends TargetField {
  override def getName: String = name
  override def isSimple: Boolean = true
}

object SimpleField {
  implicit val simpleFieldFmt: Format[SimpleField] = Json.format[SimpleField]
}

case class GroupField(group: String, fields: Seq[TargetField]) extends TargetField {
  override def getName: String = group
  override def isSimple: Boolean = false
}

object GroupField {
  implicit val groupFieldFmt: Format[GroupField] = Json.format[GroupField]
}
