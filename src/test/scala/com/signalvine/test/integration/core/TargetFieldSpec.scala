package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.JsonMatchers

class TargetFieldSpec extends Specification with JsonMatchers {
  "TargetField" should {
    val name = "Foo"
    val typ = "Bar"
    val id = "TARGET_ID"

    "serialize into JSON with all fields provided" >> {
      val o = new TargetField(name, typ, id)
      val json = Json.toJson(o).toString

      json must /("name", name)
      json must /("type", typ)
      json must /("id", id)
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "name": "$name",
           |  "type": "$typ",
           |  "id": "$id"
           |}""".stripMargin
      )
      val o = Json.fromJson[TargetField](json).get

      o.name mustEqual name
      o.typ mustEqual typ
      o.id mustEqual id
    }
  }
}
