package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.execute.Result
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}

class TargetFieldSpec extends Specification with JsonMatchers {

  def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
    fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

  def beAFieldListOf(fields: Matcher[String]*): Matcher[String] = /("fields").andHave(allOf(fields: _*))

  val simpleField: TargetField = new SimpleField("Name", "name", "String")
  val groupField: TargetField = new GroupField("Address",
    Seq(
      simpleField,
      new SimpleField("City", "city", "String")
    )
  )

  "TargetField" should {
    "Serialize SimpleField into json with all the fields" >> {
      val json = Json.toJson(simpleField).toString
      json must /("name", "Name")
      json must /("map", "name")
      json must /("type", "String")
    }

    "Serialize groupField into Json with all the fields" >> {
      val json = Json.toJson(groupField).toString
      json must /("group", "Address")
      json must beAFieldListOf(anObjectWith("name" -> "City", "type" -> "String", "map" -> "city"))
      json must beAFieldListOf(anObjectWith("name" -> "Name", "type" -> "String", "map" -> "name"))
    }

    "Deserialize into simpleField with all the fields" >> {
      val json = Json.parse(s"""{"name": "Foo", "type": "String", "map": "Bar"}""")
      val o: TargetField = Json.fromJson[TargetField](json).get
      o.getName mustEqual "Foo"
      o.asInstanceOf[SimpleField].`type` mustEqual "String"
      o.asInstanceOf[SimpleField].map mustEqual "Bar"
    }
    "Deserialize into GroupField with all the fields" >> {
      val json = Json.parse(
        s"""{"group":"Address", "fields":[
          ${
          groupField.asInstanceOf[GroupField].fields.map(
            f => {
              if (f.isSimple) {
                s"""{"name": "${f.asInstanceOf[SimpleField].name}",
                    "type": "${f.asInstanceOf[SimpleField].`type`}",
                    "map": "${f.asInstanceOf[SimpleField].map}"}"""
              } else {
                s"""{"group": "${f.asInstanceOf[GroupField].group}",
                     "fields": "${f.asInstanceOf[GroupField].fields}"
                     }"""
              }
            }
          ).mkString(",")
        }
        ]}"""
      )
      val o: TargetField = Json.fromJson[TargetField](json).get
      val innerFields = o.asInstanceOf[GroupField].fields
      o.getName mustEqual "Address"
      Result.foreach(1 to o.asInstanceOf[GroupField].fields.size) { i =>
        o.asInstanceOf[GroupField].fields(i).asInstanceOf[SimpleField].name mustEqual
          groupField.asInstanceOf[GroupField].fields(i).getName
        o.asInstanceOf[GroupField].fields(i).asInstanceOf[SimpleField].`type` mustEqual
          groupField.asInstanceOf[GroupField].fields(i).asInstanceOf[SimpleField].`type`
        o.asInstanceOf[GroupField].fields(i).asInstanceOf[SimpleField].map mustEqual
          groupField.asInstanceOf[GroupField].fields(i).asInstanceOf[SimpleField].map
      }
      innerFields(0).asInstanceOf[SimpleField].`type` mustEqual "String"
      innerFields(0).asInstanceOf[SimpleField].map mustEqual "name"
    }
  }
}
