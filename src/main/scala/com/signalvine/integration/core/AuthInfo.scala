package com.signalvine.integration.core

import play.api.data.validation.ValidationError
import play.api.libs.json._

case class AuthInfo(name: String, value: String)

object AuthInfo {
  implicit val authInfoWrite: Writes[AuthInfo] = new Writes[AuthInfo] {
    override def writes(authInfo: AuthInfo): JsValue = Json.obj(
      authInfo.name -> authInfo.value
    )
  }

  implicit val authInfoRead: Reads[AuthInfo] = new Reads[AuthInfo] {
    override def reads(json: JsValue): JsResult[AuthInfo] = {
      json.validate[JsObject].collect(ValidationError("AuthInfo must be a name-value pair")) {
        case jsObject: JsObject => jsObject.fields match {
          case Seq((name, JsString(str))) => AuthInfo(name, str)
        }
      }
    }
  }
}
