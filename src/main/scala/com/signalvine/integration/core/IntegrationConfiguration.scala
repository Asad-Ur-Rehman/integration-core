package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class IntegrationConfiguration(
                                     identity: IdentitySection,
                                     signalVine: SignalVineSection,
                                     map: MapSection,
                                     targetConfig: JsValue
                                   )

object IntegrationConfiguration {
  implicit val integrationConfigurationWrites: Writes[IntegrationConfiguration] = (
    (__ \ 'identity).write[IdentitySection] and
    (__ \ 'signalVine).write[SignalVineSection] and
    (__ \ 'map).write[MapSection] and
    (__ \ 'targetConfig).write[JsValue]
  ) (unlift(IntegrationConfiguration.unapply))

  implicit val integrationConfigurationReads: Reads[IntegrationConfiguration] = (
    (__ \ 'identity).read[IdentitySection] and
    (__ \ 'signalVine).read[SignalVineSection] and
    (__ \ 'map).read[MapSection] and
    (__ \ 'targetConfig).read[JsValue]
  ) (IntegrationConfiguration.apply _)
}