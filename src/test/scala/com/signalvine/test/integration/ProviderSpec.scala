package com.signalvine.test.integration

import com.signalvine.integration.ProviderFactory
import org.specs2.mutable._

class ProviderSpec extends Specification {
  "Provider" should {
    "return a provider name when get provider is called as :" >> {
      val providerName = "ellucian"
      ProviderFactory.getProvider(providerName) mustEqual providerName
    }
  }
  "Provider" should {
    "return a Provider List when get provider is called as :" >> {
      ProviderFactory.listProviders().length mustEqual 2
    }
  }
}
