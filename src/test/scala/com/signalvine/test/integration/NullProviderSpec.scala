package com.signalvine.test.integration

import com.signalvine.integration.NullProvider
import com.signalvine.integration.core._
import org.scalatest.concurrent.ScalaFutures
import org.specs2.mutable._
import play.api.libs.json.{JsValue, Json}

class NullProviderSpec extends Specification  with ScalaFutures {
  val authInfo = Seq(AuthInfo("url", "test.com"))
  val identitySection = new IdentitySection("nullProvider", "SomeoneNull", DateTime.now(), DateTime.now(), "Null Notes")
  val signalVineSection = new SignalVineSection(UUID.gen[Program](), "https://foo.com", "123",
    "1234-1234", Seq(new Field("nullProvider", "null")))
  val mapSection = new MapSection(Map("null" -> "nullProvider"), Map("signalvine" -> "SignalVineValue"))

  val targetConfig: JsValue =
    Json.parse(
      """{
            "name" : "null-provider",
            "lastProcessedId" : "123"
        }"""
    )
  val jobConfiguration = new JobConfiguration(identitySection,
    signalVineSection, mapSection, targetConfig)

  "Null Provider" should {
    "return an instance of Seq[AuthField] when getAuthFields is called whose size must be 1" >> {
      NullProvider.getAuthFields.isInstanceOf[Seq[AuthField]] mustEqual true
      NullProvider.getAuthFields.size mustEqual 1
    }
  }

    """return targetConfig with different lastProcessedId inside integrationConfiguration object
      | when execute is called""".stripMargin >> {
      whenReady(NullProvider.execute(jobConfiguration, authInfo)) { v =>
        v._2.targetConfig \ "name" mustEqual targetConfig \ "name"
        v._2.targetConfig \ "lastProcessedId" mustNotEqual  targetConfig \ "lastProcessedId"
      }
    }

    """return a map inside integrationConfiguration object when execute is called
      | which should be equal to mapSection""".stripMargin >> {
      whenReady(NullProvider.execute(jobConfiguration, authInfo)) { v =>
        v._2.map mustEqual mapSection
      }
    }

    "return signalVine inside integrationConfiguration object when execute is called" >> {
      whenReady(NullProvider.execute(jobConfiguration, authInfo)){ v =>
        v._2.signalVine mustEqual signalVineSection
    }

    "return saved/default values when properties of metadata are accessed" >> {
      NullProvider.metaData.id mustEqual "null-provider"
      NullProvider.metaData.targetSystemName mustEqual "NullProvider"
      NullProvider.metaData.description mustEqual "A null provider with no actual data"
    }
    """return jobConfiguration with targetConfig filled
      | when fillTargetConfig is called""".stripMargin >> {
      val jobConfig = new JobConfiguration(identitySection,
        signalVineSection, mapSection, null)
      whenReady(NullProvider.fillTargetConfig(jobConfig, authInfo)) { v =>
        (v.targetConfig \ "lastProcessedId").asOpt[String].get mustEqual "1234"
        (v.targetConfig \ "lookupId").asOpt[String].get mustEqual "123"
      }
    }
  }
}
