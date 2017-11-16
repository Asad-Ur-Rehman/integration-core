package com.signalvine.integration.core

case class Audit(auditId: UUID[Audit],
                 syncJobId: UUID[SyncJob],
                 event: EventType,
                 by: String,
                 time: DateTime,
                 jobConfiguration: JobConfiguration,
                 result: Option[Result]
                )

sealed trait  EventType{
  def value: String
}

case object IntegrationStarted extends EventType{val value = "IntegrationStarted"}
case object IntegrationSucceeded extends EventType{val value = "IntegrationSucceeded"}
case object IntegrationFailed extends EventType{val value = "IntegrationFailed"}
case object IntegrationTimeout extends EventType{val value = "IntegrationTimeout"}
case object IntegrationDeactivated extends EventType{val value = "IntegrationDeactivated"}
case object IntegrationActivated extends EventType{val value = "IntegrationActivated"}
case object IntegrationPending extends EventType{val value = "IntegrationPending"}
case object ConfigCreated extends EventType{val value = "ConfigCreated"}
case object ConfigDeleted extends EventType{val value = "ConfigDeleted"}
case object ConfigUpdated extends EventType{val value = "ConfigUpdated"}
