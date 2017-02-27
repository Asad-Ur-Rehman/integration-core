package com.signalvine.test.integration

import com.google.inject.Guice
import com.google.inject.name.Names
import com.signalvine.integration.{GuiceModule, NullProvider}
import com.signalvine.integration.core._
import org.specs2.mutable._
import net.codingwell.scalaguice.InjectorExtensions._


class GuiceModuleSpec extends Specification {
  private val injector = Guice.createInjector(new GuiceModule())
  "Guice Module" should {
    "return that instance of IntegrationProvider which is named in the injector" >> {
      injector.instance[IntegrationProvider](Names.named("null-provider")) mustEqual NullProvider
    }
    "return a list of ProviderIdentity when it is injected" >> {
      injector.instance[List[ProviderIdentity]].isInstanceOf[List[ProviderIdentity]]
    }
  }
}
