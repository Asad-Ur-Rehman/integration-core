package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.execute.Result
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}
import org.specs2.mutable.Specification
import play.api.libs.json.Json


class SyncJobSpec extends Specification with JsonMatchers {

  def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
    fields.map {
      case (name, matcher) => /(name).andHave(matcher): Matcher[String]
    }.reduceLeft(_ and _)

  def haveObject(name: String, fields: Matcher[String]*): Matcher[String] =
    /(name).andHave(eachOf(fields: _*))

  def beAFieldListOf(fields: Matcher[String]*): Matcher[String] =
    (/("jobConfiguration") / ("signalVine") / ("fields")).andHave(allOf(fields: _*))

  "SyncJob" should {
    val integrationId = UUID.gen[Integration]()
    val programId = UUID.gen[Program]()
    val accountId = UUID.gen[Account]()

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
      url,
      token,
      secret,
      Seq[Field](
        new Field("Foo", "Bar")
      )
    )
    val targetConfig = Json.parse("""{"foo": {"unicorn": "pony"}, "array": [1,2,3,4]}""")
    val jobConfiguration = new JobConfiguration(identitySection, signalVineSection, mapSection, targetConfig)

    val nextRunTime = DateTime.now()
    val schedule = "schedule"
    val name = "SomeText"
    "serialize into JSON with all fields provided" >> {
      val o = new SyncJob(integrationId, programId, accountId, jobConfiguration,"SomeText",Some("SomeDescription"), nextRunTime, schedule, Some(IntegrationStarted))
      val json = Json.toJson(o).toString
      json must /("integrationId", integrationId.toString)
      json must /("programId", programId.toString)
      json must /("accountId", accountId.toString)
      json must haveObject("jobConfiguration",
        haveObject("identity", /("providerId", identitySection.providerId)) and
          haveObject("identity", /("modified", identitySection.modified.toString)) and
          haveObject("identity", /("created", identitySection.created.toString)) and
          haveObject("identity", /("createdBy", identitySection.createdBy.toString)) and
          haveObject("identity", /("notes", identitySection.notes))
      )
      json must haveObject("jobConfiguration",
        haveObject("signalVine", /("programId", signalVineSection.programId.toString)) and
          haveObject("signalVine", /("url", signalVineSection.url)) and
          haveObject("signalVine", /("token", signalVineSection.token)) and
          haveObject("signalVine", /("secret", signalVineSection.secret))


      )
      json must beAFieldListOf(anObjectWith("name" -> "Foo", "type" -> "Bar"))

      Result.foreach(1 to mapSection.in.size) { i =>
        json must /("jobConfiguration") / ("map") / ("in") / (s"in$i", mapSection.in(s"in$i"))
      }
      Result.foreach(1 to mapSection.out.size) { o =>
        json must /("jobConfiguration") / ("map") / ("out") / (s"out$o", mapSection.out(s"out$o"))
      }
      json must /("nextRunTime", nextRunTime.toString)
      json must /("schedule", schedule)
      json must /("name", name)
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "integrationId":"${integrationId}",
           |  "programId":"${programId}",
           |  "accountId":"${accountId}",
           |  "jobConfiguration":{
           |    "identity": {
           |      "providerId":"${identitySection.providerId}",
           |      "createdBy":"${identitySection.createdBy}",
           |      "created":"${identitySection.created.toString}",
           |      "modified":"${identitySection.modified.toString}",
           |      "notes":"${identitySection.notes}"
           |    },
           |    "map": {
           |      "in": {
           |        "in1": "foo"
           |      },
           |      "out": {
           |        "out1": "bar"
           |      }
           |    },
           |    "signalVine": {
           |      "programId": "${signalVineSection.programId.toString}",
           |      "url" : "$url",
           |      "token" : "$token",
           |      "secret" : "$secret",
           |      "fields": [
           |        ${signalVineSection.fields.map(f => s"""{"name": "${f.name}", "type": "${f.`type`}"}""").mkString(",")}
           |      ]
           |    },
           |    "targetConfig": {
           |      "foo": {
           |        "unicorn": "pony"
           |      },
           |      "array": [1, 2, 3, 4]
           |    }
           |  },
           |  "nextRunTime":"${nextRunTime.toString}",
           |  "schedule":"${schedule}",
           |  "name" : "SomeText"
           |}""".stripMargin)
      val o = Json.fromJson[SyncJob](json).get

      o.integrationId mustEqual integrationId
      o.accountId mustEqual accountId
      o.programId mustEqual programId
      o.nextRunTime mustEqual nextRunTime
      o.schedule mustEqual schedule
      o.jobConfiguration.identity.providerId mustEqual identitySection.providerId
      o.jobConfiguration.identity.created mustEqual identitySection.created
      o.jobConfiguration.identity.modified mustEqual identitySection.modified
      o.jobConfiguration.identity.createdBy mustEqual identitySection.createdBy
      o.jobConfiguration.identity.notes mustEqual identitySection.notes

      Result.foreach(1 to mapSection.in.size) { i =>
        o.jobConfiguration.map.in(s"in$i") mustEqual mapSection.in(s"in$i")
      }

      Result.foreach(1 to mapSection.out.size) { i =>
        o.jobConfiguration.map.out(s"out$i") mustEqual mapSection.out(s"out$i")
      }

      o.jobConfiguration.signalVine.programId mustEqual signalVineSection.programId
      o.jobConfiguration.signalVine.url mustEqual signalVineSection.url
      o.jobConfiguration.signalVine.token mustEqual signalVineSection.token
      o.jobConfiguration.signalVine.secret mustEqual signalVineSection.secret
      Result.foreach(signalVineSection.fields.indices) { i =>
        o.jobConfiguration.signalVine.fields(i).name mustEqual signalVineSection.fields(i).name
        o.jobConfiguration.signalVine.fields(i).`type` mustEqual signalVineSection.fields(i).`type`
      }

      o.jobConfiguration.targetConfig.toString mustEqual targetConfig.toString
    }
  }
}

