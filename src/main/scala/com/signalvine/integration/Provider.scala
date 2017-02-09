package com.signalvine.integration

object ProviderFactory extends IntegrationProviderFactory {

  def listProviders(): List[String] = {
    "ellucian" :: "peoplesoft" :: Nil
  }

  def getProvider(id: String): String = {
    id
  }

}

trait IntegrationProviderFactory {
  def listProviders(): List[String]

  def getProvider(id: String): String
}