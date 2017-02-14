package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.execute.Result
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}

class SignalVineSectionSpec extends Specification with JsonMatchers {
  "SignalVineSection" should {
    val fields = Seq[Field](
      new Field("Foo", "Bar"),
      new Field("Baz", "Hal")
    )
    val programId = UUID.gen[Program]()

    def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
      fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

    def beAFieldListOf(fields: Matcher[String]*): Matcher[String] =
      /("fields").andHave(allOf(fields : _*))

    "serialize into JSON with all fields provided" >> {
      val o = new SignalVineSection(programId, fields)
      val json = Json.toJson(o).toString

      json must /("programId", programId.toString)
      json must beAFieldListOf(anObjectWith("name" -> "Foo", "type" -> "Bar"))
      json must beAFieldListOf(anObjectWith("name" -> "Baz", "type" -> "Hal"))
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "programId": "${programId.toString}",
           |  "fields": [
           |    ${fields.map(f => s"""{"name": "${f.name}", "type": "${f.`type`}"}""").mkString(",")}
           |  ]
           |}""".stripMargin)
      val o = Json.fromJson[SignalVineSection](json).get

      o.programId mustEqual programId
      Result.foreach(fields.indices) { i =>
        o.fields(i).name mustEqual fields(i).name
        o.fields(i).`type` mustEqual fields(i).`type`
      }
    }
  }
}
