package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}
import org.specs2.mutable.Specification
import play.api.libs.json.Json

class ResultSpec extends Specification with JsonMatchers {

  def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
    fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

  def beSyncErrorListOf(syncErrors: Matcher[String]*): Matcher[String] = /("syncErrors").andHave(allOf(syncErrors: _*))

  "Result" should {
    val startTime = DateTime.now
    val endTime = DateTime.now
    val updated = 100
    val imported = 105
    val ignored = 110
    val syncError1 =  SyncError(UUID.gen[Participant], "Something unexpected happened", "Something unexpected happened")
    val syncError2 =  SyncError(UUID.gen[Participant], "Something unexpected happened again", "Something unexpected happened again")
    val failMessage="Something went wrong"
    val failDetails="Line 177"
    val syncErrors = Seq[SyncError](
      syncError1,
      syncError2
    )
    val successResult: Result = SuccessResult(startTime, endTime, updated, imported, ignored, syncErrors)
    val failureResult: Result = FailureResult(startTime, endTime, failMessage, failDetails)

    "serialize SuccessResult into json with all the fields" >> {
      val js = Json.toJson(Seq(1, 2, 3, 4))
      val json = Json.toJson(successResult).toString
      json must /("startTime", startTime.toString)
      json must /("endTime", endTime.toString)
      json must /("updated", updated)
      json must /("imported", imported)
      json must /("ignored", ignored)
      json must beSyncErrorListOf(anObjectWith(
        "participantId" -> syncError1.participantId.toString,
        "message" -> syncError1.message,
        "details" -> syncError1.details
      ))
    }
    "serialize FailureResult into json with all the fields" >> {
      val json = Json.toJson(failureResult).toString
      json must /("startTime", startTime.toString)
      json must /("endTime", endTime.toString)
      json must /("message", failMessage)
      json must /("details", failDetails)
    }
    "Deserialize into SuccessResult with all the fields" >> {
      val json = Json.parse(
        s""" {
           | "startTime": "${startTime.toString}",
           | "endTime": "${endTime.toString}",
           | "updated": ${updated.toString},
           | "ignored": ${ignored.toString},
           | "imported":${imported.toString},
           | "syncErrors": [${syncErrors.map( e =>
              s"""{
                  |"participantId": "${e.participantId.toString}",
                  |"message": "${e.message}",
                  |"details": "${e.details}"
              }""".stripMargin).mkString(",")}]
        }""".stripMargin)
      val o: Result = Json.fromJson[Result](json).get
      o.asInstanceOf[SuccessResult].startTime mustEqual startTime
      o.asInstanceOf[SuccessResult].endTime mustEqual endTime
      o.asInstanceOf[SuccessResult].updated mustEqual updated
      o.asInstanceOf[SuccessResult].ignored mustEqual ignored
      o.asInstanceOf[SuccessResult].imported mustEqual imported
      o.asInstanceOf[SuccessResult].syncErrors mustEqual syncErrors
    }

    "Deserialize into FailureResult with all the fields" >> {
      val json = Json.parse(
        s""" {
           | "startTime": "${startTime.toString}",
           | "endTime": "${endTime.toString}",
           | "message": "${failMessage}",
           | "details": "${failDetails}"
         }""".stripMargin)
      val o: Result = Json.fromJson[Result](json).get
      o.asInstanceOf[FailureResult].startTime mustEqual startTime
      o.asInstanceOf[FailureResult].endTime mustEqual endTime
      o.asInstanceOf[FailureResult].message mustEqual failMessage
      o.asInstanceOf[FailureResult].details mustEqual failDetails
    }
  }
}
