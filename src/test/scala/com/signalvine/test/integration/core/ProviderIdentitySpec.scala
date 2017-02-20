package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.execute.Result
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}

class ProviderIdentitySpec extends Specification with JsonMatchers {
  "ProviderIdentity" should {
    val id = "PROVIDER_ID"
    val auth = Seq[AuthField](
      new AuthField(AuthFieldType.inputArea, "Auth0", Some("Value0")),
      new AuthField(AuthFieldType.inputBox, "Auth1", Some("Value1"))
    )

    def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
      fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

    def beAListOf(fields: Matcher[String]*): Matcher[String] =
      /("auth").andHave(allOf(fields: _*))

    "serialize into JSON with all fields provided" >> {
      val o = new ProviderIdentity(id, auth)
      val json = Json.toJson(o).toString

      json must /("id", id)
      json must beAListOf(anObjectWith("name" -> "Auth0", "pattern" -> "Value0"))
      json must beAListOf(anObjectWith("name" -> "Auth1", "pattern" -> "Value1"))
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "id": "$id",
           |  "auth": [
           |    ${auth.map(a => s"""{"type":"${a.typ}","name": "${a.name}", "pattern": "${a.pattern.getOrElse("")}"}""").mkString(",")}
           |  ]
           |}""".stripMargin
      )
      val o = Json.fromJson[ProviderIdentity](json).get

      o.id mustEqual id
      Result.foreach(auth.indices) { i =>
        o.auth(i).name mustEqual auth(i).name
        o.auth(i).pattern mustEqual auth(i).pattern
        o.auth(i).typ mustEqual auth(i).typ
      }
    }
  }
}
