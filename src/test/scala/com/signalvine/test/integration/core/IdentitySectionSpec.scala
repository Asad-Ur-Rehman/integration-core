package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}

class IdentitySectionSpec extends Specification with JsonMatchers {

  def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
    fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

  "IdentitySection" should {

    val providerId = "PROVIDER_ID"
    val createdBy = "Foo Bar"
    val created = DateTime.now()
    val modified = DateTime.now()
    val notes = "Additional Notes"

    "serialize into JSON with all fields provided" >> {

      val o = new IdentitySection(providerId, createdBy, created, modified, notes)
      val json = Json.toJson(o).toString
      json must anObjectWith(
        "providerId" -> providerId,
        "created" -> created.toString(),
        "modified" -> modified.toString(),
        "createdBy" -> createdBy,
        "notes" -> notes
      )
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "providerId":"$providerId",
           |  "createdBy":"$createdBy",
           |  "created":"${created.toString()}",
           |  "modified":"${modified.toString()}",
           |  "notes":"$notes"
           |}""".stripMargin)
      val o = Json.fromJson[IdentitySection](json).get

      o.providerId mustEqual providerId
      o.created mustEqual created
      o.modified mustEqual modified
      o.createdBy mustEqual createdBy
      o.notes mustEqual notes
    }
  }
}