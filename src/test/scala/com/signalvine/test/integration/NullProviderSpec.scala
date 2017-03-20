package com.signalvine.test.integration

import com.signalvine.integration.NullProvider
import com.signalvine.integration.core._
import org.specs2.mutable._
import play.api.libs.json.{JsValue, Json}

class NullProviderSpec extends Specification {

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

    """return targetConfig with different lastProcessedId inside integrationConfiguration object
      | when execute is called""".stripMargin >> {
      NullProvider.execute(jobConfiguration).targetConfig \ "name" mustEqual
        targetConfig \ "name"
      NullProvider.execute(jobConfiguration).targetConfig \ "lastProcessedId" mustNotEqual
        targetConfig \ "lastProcessedId"
    }

    """return a map inside integrationConfiguration object when execute is called
      | which should be equal to mapSection""".stripMargin >> {
      NullProvider.execute(jobConfiguration).map mustEqual mapSection
    }

    "return signalVine inside integrationConfiguration object when execute is called" >> {
      NullProvider.execute(jobConfiguration).signalVine mustEqual signalVineSection
    }

    "return saved/default values when properties of metadata are accessed" >> {
      NullProvider.metaData.id mustEqual "null-provider"
      NullProvider.metaData.targetSystemName mustEqual "NullProvider"
      NullProvider.metaData.description mustEqual "A null provider with no actual data"
    }
    """return jobConfiguration with targetConfig filled
      | when fillTargetConfig is called""".stripMargin >> {
      val authInfo = Json.parse(
        """[{"url":"test.com"}]"""
      )
      val obj = Json.fromJson[Seq[AuthInfo]](authInfo).get
      val jobConfig = new JobConfiguration(identitySection,
        signalVineSection, mapSection, null)
      NullProvider.fillTargetConfig(jobConfig, obj) must beSuccessfulTry
    }
  }
}
