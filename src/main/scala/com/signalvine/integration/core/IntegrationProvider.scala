package com.signalvine.integration.core

import scala.concurrent.Future

trait IntegrationProvider {
  val id: String
  val metaData: ProviderMetadata

  def getAuthFields: Seq[AuthField]
  def listFields(auth: Seq[AuthInfo]): Future[Seq[TargetField]]
  def execute(jobConfiguration: JobConfiguration, authenticationInfo: Seq[AuthInfo]): Future[(Result, JobConfiguration)]
  def fillTargetConfig(
                        jobConfiguration: JobConfiguration,
                        authenticationInfo: Seq[AuthInfo],
                        authoritativeSource : Option[String] =Option.empty[String],
                        dateModifiedColumn : Option[String] =Option.empty[String]
                      ): Future[JobConfiguration]
}

case class ProviderException(message: String, inner: Option[Throwable]) extends Exception(message, inner.orNull)
case class ProviderAuthenticationException(message: String, inner: Option[Throwable]) extends Exception(message, inner.orNull)
case class NetworkTimeoutException(message: String, inner: Option[Throwable]) extends Exception(message, inner.orNull)
case class NetworkUnreachableException(message: String, inner: Option[Throwable]) extends Exception(message, inner.orNull)
