package com.signalvine.test.integration.core

import com.signalvine.integration.core.AuthInfo
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import play.api.libs.json.Json

class AuthInfoSpec extends Specification with JsonMatchers {
  val name = "Auth_info"
  val value = "anything"
  "AuthInfo" should {
    "serialize into JSON with all fields provided" >> {
      val o = new AuthInfo(name, value)
      val json = Json.toJson(o).toString
      json must /("name", name)
      json must /("value", value)
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{"name":"$name","value":"$value"}"""
      )
      val o = Json.fromJson[AuthInfo](json).get
      o.name mustEqual name
      o.value mustEqual value
    }

  }
}
