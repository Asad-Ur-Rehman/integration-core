package com.signalvine.integration.core

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait Integration

sealed trait Account

case class SyncJob(integrationId: UUID[Integration],
                   programId: UUID[Program],
                   accountId: UUID[Account],
                   jobConfiguration: JobConfiguration,
                   name: String,
                   description: Option[String],
                   nextRunTime: DateTime,
                   schedule: String,
                   eventType: Option[EventType] = Option.empty[EventType])

object SyncJob {
  //eventType is purposefully ignored by both syncjob reader and writer because it is not required here.
  implicit val syncJobWrites: Writes[SyncJob] = new Writes[SyncJob] {
    override def writes(o: SyncJob): JsValue = Json.obj(
      "integrationId" -> o.integrationId,
      "programId" -> o.programId,
      "accountId" -> o.accountId,
      "jobConfiguration" -> o.jobConfiguration,
      "name" -> o.name,
      "description" -> o.description,
      "nextRunTime" -> o.nextRunTime,
      "schedule" -> o.schedule
    )
  }

   val syncJobWritesWithTokenAndSecret: Writes[SyncJob] = new Writes[SyncJob] {
    override def writes(o: SyncJob): JsValue = Json.obj(
      "integrationId" -> o.integrationId,
      "programId" -> o.programId,
      "accountId" -> o.accountId,
      "jobConfiguration" -> Json.toJson[JobConfiguration](o.jobConfiguration)(JobConfiguration.jobConfigurationWriteWithTokenSecret),
      "name" -> o.name,
      "description" -> o.description,
      "nextRunTime" -> o.nextRunTime,
      "schedule" -> o.schedule
    )
  }

  implicit val syncJobReads: Reads[SyncJob] = (
    (__ \ 'integrationId).read[UUID[Integration]] and
      (__ \ 'programId).read[UUID[Program]] and
      (__ \ 'accountId).read[UUID[Account]] and
      (__ \ 'jobConfiguration).read[JobConfiguration] and
      (__ \ 'name).read[String] and
      (__ \ 'description).readNullable[String] and
      (__ \ 'nextRunTime).read[DateTime] and
      (__ \ 'schedule).read[String] and
      (__ \ 'eventType).read(Option.empty[EventType])
    ) (SyncJob.apply _)
}
