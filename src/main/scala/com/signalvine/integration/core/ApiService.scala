package com.signalvine.integration.core

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ApiService {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val apiClient = AhcWSClient()

  def terminate(): Unit = {
    apiClient.close
    materializer.shutdown
    system.terminate
  }
}

object ApiService {
  def withClientAsync[T, Q <: ApiService](op: Q => Future[T], errorHandler: Throwable => T)(apiService: Q): Future[T] = {
    op(apiService).map {
      obj => {
        apiService.terminate
        obj
      }
    }.recover {
      case error =>
        apiService.terminate
        errorHandler(error)
    }
  }

  def withClient[T, Q <: ApiService](op: Q => T, errorHandler: Throwable => T)(apiService: Q): T = {
    try {
      op(apiService)
    } catch {
      case e: Throwable => errorHandler(e)
    } finally {
      apiService.terminate
    }
  }
}
