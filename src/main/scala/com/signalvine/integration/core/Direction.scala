package com.signalvine.integration.core

sealed abstract class Direction(name: String) {
  override def toString: String = name
}

object IntegrationDirections {
  case object in extends Direction("in")
  case object out extends Direction("out")
}
