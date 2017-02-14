package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class MapSection(
                       in: Map[String, String],
                       out: Map[String, String]
                     )

object MapSection {
  implicit val mapSectionWrite: Writes[MapSection] = (
    (__ \ 'in).write[Map[String, String]] and
    (__ \ 'out).write[Map[String, String]]
  ) (unlift(MapSection.unapply))

  implicit val mapSectionRead: Reads[MapSection] = (
    (__ \ 'in).read[Map[String, String]] and
    (__ \ 'out).read[Map[String, String]]
  ) (MapSection.apply _)
}