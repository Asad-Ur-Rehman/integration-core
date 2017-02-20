package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.execute.Result
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}

class NamedFieldListSpec extends Specification with JsonMatchers {
  "NamedFieldList" should {

    val name = "FooBar"
    val fields = new Lists.FieldList(1)
    fields(0) = new TargetField("name0", "type0", "id0")

    def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
      fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

    def beAFieldListOf(fields: Matcher[String]*): Matcher[String] =
      /("fields").andHave(allOf(fields: _*))

    "serialize into JSON with all fields provided" >> {
      val o = new NamedFieldList(name, fields)
      val json = Json.toJson(o).toString

      json must /("name", name)
      json must beAFieldListOf(anObjectWith("name" -> "name0", "type" -> "type0", "id" -> "id0"))
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "name": "${name.toString}",
           |  "fields": [
           |    ${fields.map(f => s"""{"name": "${f.name}","type": "${f.typ}","id": "${f.id}"}""").mkString(",")}
           |  ]
           |}""".stripMargin)
      val o = Json.fromJson[NamedFieldList](json).get

      o.name mustEqual name
      Result.foreach(fields.indices) { i =>
        o.fields(i).name mustEqual fields(i).name
        o.fields(i).typ mustEqual fields(i).typ
        o.fields(i).id mustEqual fields(i).id
      }
    }
  }
}
