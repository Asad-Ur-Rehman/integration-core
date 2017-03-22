package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

abstract sealed class AuthFieldType(val name: String)

object AuthFieldType {
  case object inputBox extends AuthFieldType("inputBox")
  case object inputArea extends AuthFieldType("inputArea")
  case object url extends AuthFieldType("url")

  implicit val reads: Reads[AuthFieldType] = Reads {
    case JsString(inputBox.name) => JsSuccess(inputBox)
    case JsString(inputArea.name) => JsSuccess(inputArea)
    case JsString(url.name) => JsSuccess(url)
    case unknown => JsError(s"Invalid AuthFieldType priority: ${Json.stringify(unknown)}")
  }

  implicit val writes: Writes[AuthFieldType] = Writes(o =>
    JsString(o.name)
  )
}

case class AuthField(typ: AuthFieldType, name: String, pattern: Option[String])

object AuthField {
  implicit val optionStringWrites: Writes[Option[String]] = Writes.optionWithNull[String]
  implicit val optionStringReads: Reads[Option[String]] = Reads.optionWithNull[String]

  implicit val authFieldWrite: Writes[AuthField] = (
    (__ \ 'type).write[AuthFieldType] and
    (__ \ 'name).write[String] and
    (__ \ 'pattern).write(optionStringWrites)
  ) (unlift(AuthField.unapply))

  implicit val authFieldRead: Reads[AuthField] = (
    (__ \ 'type).read[AuthFieldType] and
    (__ \ 'name).read[String] and
    (__ \ 'pattern).read(optionStringReads)
  ) (AuthField.apply _)
}

case class ProviderIdentity(id: String, auth: Seq[AuthField])

object ProviderIdentity {
  implicit val providerIdentityWrite: Writes[ProviderIdentity] = (
    (__ \ 'id).write[String] and
    (__ \ 'auth).write[Seq[AuthField]]
  ) (unlift(ProviderIdentity.unapply))

  implicit val providerIdentityRead: Reads[ProviderIdentity] = (
    (__ \ 'id).read[String] and
    (__ \ 'auth).read[Seq[AuthField]]
  ) (ProviderIdentity.apply _)
}

