package com.signalvine.integration

import com.signalvine.integration.core._
import play.api.libs.json._

import scala.util.Try

object NullProvider extends IntegrationProvider {
  override val id: String = "null-provider"
  override val metaData: ProviderMetadata = ProviderMetadata(
    id, "NullProvider", "A null provider with no actual data"
  )

  override def getAuthFields: Seq[AuthField] = Seq(
    new AuthField(AuthFieldType.inputBox, "token", Some("[0-9]+"))
  )

  override def listFields(authenticationInfo: Seq[AuthInfo]): Try[Seq[TargetField]] = {
    Try {
      if (authenticateProvider(authenticationInfo)) {
        Seq(new SimpleField("First Name", "first.name", "String"),
          new GroupField("address",
            Seq(new SimpleField("Street No.", "street.no", "String"),
              new SimpleField("City", "city", "String"),
              new SimpleField("Country", "country", "String")
            )
          )
        )
      } else {
        throw ProviderAuthenticationException("Unable to authenticate. Check your credentials")
      }
    }
  }


  override def execute(conf: JobConfiguration): (Result, JobConfiguration) = {
    val identitySection = conf.identity
    val signalVineSection = conf.signalVine
    val mapSection = conf.map
    val targetConfig = conf.targetConfig.as[JsObject] ++ Json.obj("lastProcessedId" -> "777")
    val syncError1 =  SyncError(UUID.gen[Participant], "Something unexpected happened", "Something unexpected happened")
    val syncError2 =  SyncError(UUID.gen[Participant], "Something unexpected happened again", "Something unexpected happened again")
    val syncErrors = Seq[SyncError](
      syncError1,
      syncError2
    )
    val successResult: Result = SuccessResult(DateTime.now, DateTime.now, 100, 100, 77, syncErrors)

    (successResult, new JobConfiguration(identitySection, signalVineSection, mapSection, targetConfig))
  }

  override def fillTargetConfig(jobConfiguration: JobConfiguration, authenticationInfo: Seq[AuthInfo]):
  Try[JobConfiguration] = {
    Try {
      if (authenticateProvider(authenticationInfo)) {
        val targetConfig = Json.parse("""{"lastProcessedId": "1234", "lookupId": "123"}""")
        JobConfiguration(
          jobConfiguration.identity,
          jobConfiguration.signalVine,
          jobConfiguration.map,
          targetConfig
        )
      }
      else {
        throw ProviderAuthenticationException("Unable to authenticate. Check your credentials")
      }
    }
  }

  def authenticateProvider(authenticationInfo: Seq[AuthInfo]): Boolean = {
    authenticationInfo.head.name match {
      case "url" => authenticationInfo.head.value match {
        case "test.com" => true
        case _ => false
      }
      case _ => false
    }
  }
}
