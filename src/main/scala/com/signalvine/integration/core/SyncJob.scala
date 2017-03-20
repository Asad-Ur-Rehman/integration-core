package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait Integration

sealed trait Account

case class SyncJob(integrationId: UUID[Integration],
                   programId: UUID[Program],
                   accountId: UUID[Account],
                   jobConfiguration: JobConfiguration,
                   nextRunTime: DateTime,
                   schedule: String)

object SyncJob {
  implicit val syncJobWrites: Writes[SyncJob] = (
    (__ \ 'integrationId).write[UUID[Integration]] and
      (__ \ 'programId).write[UUID[Program]] and
      (__ \ 'accountId).write[UUID[Account]] and
      (__ \ 'jobConfiguration).write[JobConfiguration] and
      (__ \ 'nextRunTime).write[DateTime] and
      (__ \ 'schedule).write[String]
    ) (unlift(SyncJob.unapply))

  implicit val syncJobReads: Reads[SyncJob] = (
    (__ \ 'integrationId).read[UUID[Integration]] and
      (__ \ 'programId).read[UUID[Program]] and
      (__ \ 'accountId).read[UUID[Account]] and
      (__ \ 'jobConfiguration).read[JobConfiguration] and
      (__ \ 'nextRunTime).read[DateTime] and
      (__ \ 'schedule).read[String]
    ) (SyncJob.apply _)
}