package com.signalvine.integration

import com.google.inject.{ConfigurationException, Guice}
import com.google.inject.name.Names
import com.signalvine.integration.core.{IntegrationProvider, ProviderIdentity}
import net.codingwell.scalaguice.InjectorExtensions._
import org.slf4j.LoggerFactory

trait ProviderFactory {
  def listProviders(): List[ProviderIdentity]

  def getProvider(id: String): Option[IntegrationProvider]
}

object ProviderFactory extends ProviderFactory {
  def logger = LoggerFactory.getLogger(this.getClass)
  private val injector = Guice.createInjector(new GuiceModule())

  override def listProviders(): List[ProviderIdentity] = {
    injector.instance[List[ProviderIdentity]]
  }

  override def getProvider(id: String): Option[IntegrationProvider] = {
    try {
      Some(injector.instance[IntegrationProvider](Names.named(id)))
    } catch {
      case e: ConfigurationException =>
        logger.warn(s"Could not find provider with id $id")
        None
    }
  }
}
