package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class JobConfiguration(
    identity: IdentitySection,
    signalVine: SignalVineSection,
    map: MapSection,
    targetConfig: JsValue
    )

object JobConfiguration {
  implicit val jobConfigurationWrites: Writes[JobConfiguration] = (
    (__ \ 'identity).write[IdentitySection] and
    (__ \ 'signalVine).write[SignalVineSection] and
    (__ \ 'map).write[MapSection] and
    (__ \ 'targetConfig).write[JsValue]
  ) (unlift(JobConfiguration.unapply))

  val jobConfigurationWriteWithTokenSecret: Writes[JobConfiguration] = (
      (__ \ 'identity).write[IdentitySection] and
      (__ \ 'signalVine).write[SignalVineSection](SignalVineSection.signalVineSectionWritesWithTokenSecret) and
      (__ \ 'map).write[MapSection] and
      (__ \ 'targetConfig).write[JsValue]
      ) (unlift(JobConfiguration.unapply))

  implicit val jobConfigurationReads: Reads[JobConfiguration] = (
    (__ \ 'identity).read[IdentitySection] and
    (__ \ 'signalVine).read[SignalVineSection] and
    (__ \ 'map).read[MapSection] and
    (__ \ 'targetConfig).read[JsValue]
  ) (JobConfiguration.apply _)
}
