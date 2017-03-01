package com.signalvine.integration

import com.signalvine.integration.core._
import play.api.libs.json._

object NullProvider extends IntegrationProvider {
  override val id: String = "null-provider"
  override val metaData: ProviderMetadata = ProviderMetadata(
    id, "NullProvider", "A null provider with no actual data"
  )

  override def getAuthFields: Seq[AuthField] = Seq(
    new AuthField(AuthFieldType.inputBox, "token", Some("[0-9]+"))
  )

  override def listFields(auth: Seq[AuthInfo]): Lists.FieldList = Array(
    new TargetField("first.name", "string", "firstname"),
    new TargetField("last.name", "string", "lastname")
  )

  override def execute(conf: IntegrationConfiguration): IntegrationConfiguration = {
    val identitySection = conf.identity
    val signalVineSection = conf.signalVine
    val mapSection = conf.map
    val targetConfig = conf.targetConfig.as[JsObject] ++ Json.obj("lastProcessedId" -> "777")

    new IntegrationConfiguration(identitySection, signalVineSection, mapSection, targetConfig)
  }
}
