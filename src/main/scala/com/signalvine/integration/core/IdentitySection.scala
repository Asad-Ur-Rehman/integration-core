package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class IdentitySection(
                            providerId: String,
                            createdBy: String,
                            created: DateTime,
                            modified: DateTime,
                            notes: String
                          )

object IdentitySection {

  implicit val identitySectionWrite: Writes[IdentitySection] = (
    (__ \ 'providerId).write[String] and
    (__ \ 'createdBy).write[String] and
    (__ \ 'created).write[DateTime] and
    (__ \ 'modified).write[DateTime] and
    (__ \ 'notes).write[String]
  ) (unlift(IdentitySection.unapply))

  implicit val identitySectionRead: Reads[IdentitySection] = (
    (__ \ 'providerId).read[String] and
    (__ \ 'createdBy).read[String] and
    (__ \ 'created).read[DateTime] and
    (__ \ 'modified).read[DateTime] and
    (__ \ 'notes).read[String]
  ) (IdentitySection.apply _)
}
