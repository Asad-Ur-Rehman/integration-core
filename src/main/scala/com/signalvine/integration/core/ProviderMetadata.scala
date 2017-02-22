package com.signalvine.integration.core

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, Writes, _}

case class ProviderMetadata(id: String, targetSystemName: String, description: String)

object ProviderMetadata {
  implicit val ProviderMetadataWrites: Writes[ProviderMetadata] = (
    (__ \ 'id).write[String] and
    (__ \ 'targetSystemName).write[String] and
    (__ \ 'description).write[String]
  ) (unlift(ProviderMetadata.unapply))

  implicit val ProviderMetadataReads: Reads[ProviderMetadata] = (
    (__ \ 'id).read[String] and
    (__ \ 'targetSystemName).read[String] and
    (__ \ 'description).read[String]
  ) (ProviderMetadata.apply _)
}

