package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait Integration

sealed trait Account

case class SyncJob(integrationId: UUID[Integration],
                   programId: UUID[Program],
                   accountId: UUID[Account],
                   integrationConfiguration: IntegrationConfiguration,
                   nextRunTime: DateTime,
                   schedule: String)

object SyncJob {
  implicit val syncJobWrites: Writes[SyncJob] = (
    (__ \ 'integrationId).write[UUID[Integration]] and
      (__ \ 'programId).write[UUID[Program]] and
      (__ \ 'accountId).write[UUID[Account]] and
      (__ \ 'integrationConfiguration).write[IntegrationConfiguration] and
      (__ \ 'nextRunTime).write[DateTime] and
      (__ \ 'schedule).write[String]
    ) (unlift(SyncJob.unapply))

  implicit val syncJobReads: Reads[SyncJob] = (
    (__ \ 'integrationId).read[UUID[Integration]] and
      (__ \ 'programId).read[UUID[Program]] and
      (__ \ 'accountId).read[UUID[Account]] and
      (__ \ 'integrationConfiguration).read[IntegrationConfiguration] and
      (__ \ 'nextRunTime).read[DateTime] and
      (__ \ 'schedule).read[String]
    ) (SyncJob.apply _)
}