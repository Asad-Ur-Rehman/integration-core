package com.signalvine.test.integration

import com.signalvine.integration.ProviderFactory
import org.specs2.mutable._

class ProviderFactorySpec extends Specification {
  "ProviderFactory" should {
    "return that provider which is mentioned in the parameter of getProvider" >> {
      ProviderFactory.getProvider("null-provider").get.id mustEqual "null-provider"
    }
  }
  "ProviderFactory" should {
    "return none when the provider mentioned is not found" >> {
      ProviderFactory.getProvider("").isEmpty mustEqual true
    }
  }
}



