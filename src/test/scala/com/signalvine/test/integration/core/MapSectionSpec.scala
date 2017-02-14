package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.execute.Result
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.JsonMatchers

class MapSectionSpec extends Specification with JsonMatchers{
  "MapSection" should {
    val in: Map[String, String] = Map(
      "in1" -> "foo",
      "in2" -> "bar",
      "in3" -> "baz"
    )

    val out: Map[String, String] = Map(
      "out1" -> "bar",
      "out2" -> "baz",
      "out3" -> "foo"
    )

    "serialize into JSON with all fields provided" >> {

      val m = new MapSection(in, out)
      val json = Json.toJson(m).toString

      Result.foreach(1 to in.size) { i =>
        json must /("in")/(s"in$i", in(s"in$i"))
      }

      Result.foreach(1 to out.size) { o =>
        json must /("out")/(s"out$o", out(s"out$o"))
      }
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(
        s"""{
           |  "in": {
           |    "in1": "foo",
           |    "in2": "bar",
           |    "in3": "baz"
           |  },
           |  "out": {
           |    "out1": "bar",
           |    "out2": "baz",
           |    "out3": "foo"
           |  }
           |}""".stripMargin)
      val m = Json.fromJson[MapSection](json).get

      Result.foreach(1 to in.size) { i =>
        m.in(s"in$i") mustEqual in(s"in$i")
      }

      Result.foreach(1 to out.size) { o =>
        m.out(s"out$o") mustEqual out(s"out$o")
      }
    }
  }
}