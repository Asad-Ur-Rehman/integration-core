package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.mutable.Specification

sealed trait Foo

class UUIDSpec extends Specification {
  "UUID" should {

    val raw = java.util.UUID.randomUUID()

    "create a new instance with `java.util.UUID`" >> {
      val u = new UUID[Foo](raw)
      u.toString mustEqual raw.toString

      val i = UUID.fromRaw[Foo](raw)
      i.toString mustEqual raw.toString
    }

    "convert UUID instance to `java.util.UUID`" >> {
      val u = UUID.gen[Foo]()
      val r = UUID.toRaw[Foo](u)

      r.getClass.getName mustEqual "java.util.UUID"
      r.toString mustEqual u.toString
    }

    "create a new instance with a string UUID" >> {
      val s = raw.toString
      val u = UUID.fromString[Foo](s)

      u must beRight
      u.fold(x => x, y => y.toString) mustEqual s
    }

    "return an error message if UUID is in wrong format" >> {
      val s = "foo-bar"
      val u = UUID.fromString[Foo](s)

      u must beLeft
      u.fold(x => x, y => y.toString) mustEqual s"Invalid UUID string: $s"

      val r = UUID.fromStringRaw(s)
      r must beLeft
      r.fold(x => x, y => y.toString) mustEqual s"Invalid UUID string: $s"
    }
  }
}
