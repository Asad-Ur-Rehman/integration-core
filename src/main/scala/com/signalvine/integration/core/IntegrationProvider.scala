package com.signalvine.integration.core

trait IntegrationProvider {
  val id: String
  val metaData: ProviderMetadata

  def getAuthFields: Seq[AuthField]
  def listFields(auth: Seq[AuthInfo]): Lists.FieldList
  def execute(integrationConfiguration: IntegrationConfiguration): IntegrationConfiguration
}
