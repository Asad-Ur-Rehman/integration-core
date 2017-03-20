package com.signalvine.test.integration.core
import com.signalvine.integration.core.ProviderMetadata
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import play.api.libs.json.Json

class ProviderMetadataSpec extends Specification with JsonMatchers {
  val id = "null"
  val targetSystemName = "NullProvider"
  val description= "I am a null provider"
  "ProviderMetadata" should {
    "serialize into JSON with all fields provided" >> {
      val o = new ProviderMetadata(id,targetSystemName,description)
      val json = Json.toJson(o).toString
      json must /("id", id)
      json must /("targetSystemName", targetSystemName)
      json must /("description",description)
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{"id":"$id","targetSystemName":"$targetSystemName","description":"$description"}"""
      )
      val o = Json.fromJson[ProviderMetadata](json).get
      o.id mustEqual id
      o.targetSystemName mustEqual targetSystemName
      o.description mustEqual description
    }
  }
}
