package com.signalvine.integration.core

import scala.util.Try

case class Result(status: String)

trait IntegrationProvider {
  val id: String
  val metaData: ProviderMetadata

  def getAuthFields: Seq[AuthField]
  def listFields(auth: Seq[AuthInfo]): Try[Seq[TargetField]]
  def execute(jobConfiguration: JobConfiguration): (Result, JobConfiguration)
  def fillTargetConfig(jobConfiguration: JobConfiguration , authenticationInfo: Seq[AuthInfo]) : Try[JobConfiguration]
}

case class ProviderAuthenticationException(message: String) extends Exception(message)
case class NetworkTimeoutException(message: String) extends Exception(message)
case class NetworkUnreachableException(message: String) extends Exception(message)
