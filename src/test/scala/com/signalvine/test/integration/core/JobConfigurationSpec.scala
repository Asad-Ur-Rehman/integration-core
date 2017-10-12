package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.execute.Result
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}

class JobConfigurationSpec extends Specification with JsonMatchers {
  "JobConfiguration" should {
    val url = "https://foo.com"
    val secret = "1234-1234-1234-12345678-123456789012"
    val token = "1234"
    val identitySection = new IdentitySection(
      "PROVIDER_ID",
      "Foo Bar",
      DateTime.now(),
      DateTime.now(),
      "Some notes"
    )
    val mapSection = new MapSection(
      Map("in1" -> "foo"),
      Map("out1" -> "foo")
    )
    val signalVineSection = new SignalVineSection(
      UUID.fromRaw[Program](UUID.genRaw()),
      token,
      secret,
      Seq[Field](
        new Field("Foo", "Bar")
      )
    )
    val targetConfig = Json.parse("""{"foo": {"unicorn": "pony"}, "array": [1,2,3,4]}""")

    def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
      fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

    def beAFieldListOf(fields: Matcher[String]*): Matcher[String] =
      (/("signalVine")/("fields")).andHave(allOf(fields : _*))

    "serialize into JSON with all fields provided" >> {
      val o = new JobConfiguration(identitySection, signalVineSection, mapSection, targetConfig)
      val json = Json.toJson(o)(JobConfiguration.jobConfigurationWriteWithTokenSecret).toString

      json must /("identity", anObjectWith(
        "providerId" -> identitySection.providerId,
        "created" -> identitySection.created.toString,
        "modified" -> identitySection.modified.toString,
        "createdBy" -> identitySection.createdBy,
        "notes" -> identitySection.notes
      ))

      Result.foreach(1 to mapSection.in.size) { i =>
        json must /("map")/("in")/(s"in$i", mapSection.in(s"in$i"))
      }

      Result.foreach(1 to mapSection.out.size) { o =>
        json must /("map")/("out")/(s"out$o", mapSection.out(s"out$o"))
      }

      json must /("signalVine")/("programId", signalVineSection.programId.toString)
      json must /("signalVine")/("token", signalVineSection.token)
      json must /("signalVine")/("secret", signalVineSection.secret)
      json must beAFieldListOf(anObjectWith("name" -> "Foo", "type" -> "Bar"))
    }

    "serialize into JSON with all fields provided without token and secret" >> {
      val o = new JobConfiguration(identitySection, signalVineSection, mapSection, targetConfig)
      val json = Json.toJson(o).toString

      json must /("identity", anObjectWith(
        "providerId" -> identitySection.providerId,
        "created" -> identitySection.created.toString,
        "modified" -> identitySection.modified.toString,
        "createdBy" -> identitySection.createdBy,
        "notes" -> identitySection.notes
      ))

      Result.foreach(1 to mapSection.in.size) { i =>
        json must /("map")/("in")/(s"in$i", mapSection.in(s"in$i"))
      }

      Result.foreach(1 to mapSection.out.size) { o =>
        json must /("map")/("out")/(s"out$o", mapSection.out(s"out$o"))
      }

      json must /("signalVine")/("programId", signalVineSection.programId.toString)
      json must beAFieldListOf(anObjectWith("name" -> "Foo", "type" -> "Bar"))
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "identity": {
           |    "providerId":"${identitySection.providerId}",
           |    "createdBy":"${identitySection.createdBy}",
           |    "created":"${identitySection.created.toString}",
           |    "modified":"${identitySection.modified.toString}",
           |    "notes":"${identitySection.notes}"
           |  },
           |  "map": {
           |    "in": {
           |      "in1": "foo"
           |    },
           |    "out": {
           |      "out1": "bar"
           |    }
           |  },
           |  "signalVine": {
           |    "programId": "${signalVineSection.programId.toString}",
           |    "url" : "$url",
           |    "token" : "$token",
           |    "secret" : "$secret",
           |    "fields": [
           |      ${signalVineSection.fields.map(f => s"""{"name": "${f.name}", "type": "${f.`type`}"}""").mkString(",")}
           |    ]
           |  },
           |  "targetConfig": {
           |    "foo": {
           |      "unicorn": "pony"
           |    },
           |    "array": [1, 2, 3, 4]
           |  }
           |}""".stripMargin)
      val o = Json.fromJson[JobConfiguration](json).get

      o.identity.providerId mustEqual identitySection.providerId
      o.identity.created mustEqual identitySection.created
      o.identity.modified mustEqual identitySection.modified
      o.identity.createdBy mustEqual identitySection.createdBy
      o.identity.notes mustEqual identitySection.notes

      Result.foreach(1 to mapSection.in.size) { i =>
        o.map.in(s"in$i") mustEqual mapSection.in(s"in$i")
      }

      Result.foreach(1 to mapSection.out.size) { i =>
        o.map.out(s"out$i") mustEqual mapSection.out(s"out$i")
      }

      o.signalVine.programId mustEqual signalVineSection.programId
      o.signalVine.token mustEqual signalVineSection.token
      o.signalVine.secret mustEqual signalVineSection.secret
      Result.foreach(signalVineSection.fields.indices) { i =>
        o.signalVine.fields(i).name mustEqual signalVineSection.fields(i).name
        o.signalVine.fields(i).`type` mustEqual signalVineSection.fields(i).`type`
      }

      o.targetConfig.toString mustEqual targetConfig.toString
    }
  }
}
