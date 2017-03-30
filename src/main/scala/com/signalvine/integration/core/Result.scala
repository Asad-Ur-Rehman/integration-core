package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

trait Result

object Result {
  implicit val fieldFormat: Format[Result] = new Format[Result] {
    def writes(field: Result): JsValue = {
      field match {
        case b: SuccessResult => Json.toJson[SuccessResult](b)(SuccessResult.successResultFmt)
        case b: FailureResult => Json.toJson[FailureResult](b)(FailureResult.failureResultFmt)
      }
    }

    def reads(json: JsValue): JsResult[Result] = {
      json.validate[SuccessResult](SuccessResult.successResultFmt) match {
        case _: JsError => json.validate[FailureResult](FailureResult.failureResultFmt)
        case s: JsSuccess[SuccessResult] => s
      }
    }
  }
}


case class SuccessResult(startTime: DateTime, endTime: DateTime, updated: Int, imported: Int, ignored: Int, syncErrors: Seq[SyncError]) extends Result

object SuccessResult {
  implicit val successResultFmt: Format[SuccessResult] = Json.format[SuccessResult]
}

case class FailureResult(startTime: DateTime, endTime: DateTime, message: String, details: String) extends Result

object FailureResult {
  implicit val failureResultFmt: Format[FailureResult] = Json.format[FailureResult]
}