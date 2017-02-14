package com.signalvine.integration.core

import java.nio.{ByteOrder, ByteBuffer}
import java.sql.PreparedStatement

import scala.util.Try

import anorm.{ToStatement, TypeDoesNotMatch, Column}
import play.api.libs.json._

/**
  * Type-safe UUID wrapper that encodes the type it relates to.  This class explicitly hides its
  * UUID value to prevent converting it between types.
  */
case class UUID[+A](unsafeToRaw: UUID.Raw) {
  override def toString = unsafeToRaw.toString

  /** Forcibly change the phantom type. */
  def unsafeCast[B]: UUID[B] = asInstanceOf[UUID[B]]
}

object UUID {
  type Raw = java.util.UUID
  // TODO: This is a hack since play's routes syntax doesn't seem to support nested type params, e.g. Option[UUID[A]]
  // This should be resolved in play 2.4 - https://github.com/playframework/playframework/issues/2402
  type Maybe[A] = Option[UUID[A]]

  def fromRaw[A](uuid: Raw): UUID[A] = new UUID(uuid)

  def toRaw[A](u: UUID[A]): Raw = java.util.UUID.fromString(u.toString)

  def nil[A]: UUID[A] = fromRaw(new UUID.Raw(0, 0))

  def gen[A](): UUID[A] = fromRaw(genRaw())

  def genRaw(): Raw = java.util.UUID.randomUUID()

  def genString(): String = genRaw().toString

  def fromString[A](s: String): Either[String, UUID[A]] = fromStringRaw(s).right.map(fromRaw)

  def fromStringRaw(s: String): Either[String, Raw] = {
    fromStringRawOpt(s).toRight(s"Invalid UUID string: $s")
  }

  def fromStringRawOpt(s: String): Option[Raw] = {
    Try {
      java.util.UUID.fromString(s)
    }.toOption
  }

  def fromStringOpt[A](s: String): Option[UUID[A]] = fromString(s).right.toOption

  def unsafeFromString[A](s: String): UUID[A] = fromRaw(unsafeFromStringRaw(s))

  def unsafeFromStringRaw(s: String): Raw = fromStringRaw(s).fold({ e => throw new RuntimeException(e) }, identity)

  /**
    * Deterministically convert a byte array into a UUID.
    *
    * @param value byte array to convert
    * @return corresponding UUID
    */
  def fromByteArrayRaw(value: Array[Byte]): Either[String, UUID.Raw] = try {
    require(value.length == 16)
    val buffer = ByteBuffer.wrap(value)
    buffer.order(ByteOrder.BIG_ENDIAN)
    val high = buffer.getLong
    val low = buffer.getLong
    Right(new UUID.Raw(high, low))
  } catch {
    case e: Throwable => Left(e.getMessage)
  }

  /**
    * Helper to convert UUIDs into a PostgreSQL array literal.
    *
    * NOTE: Using '{..}' syntax with large arrays causes anorm's parser to stack overflow,
    * so we use 'array[..]' syntax instead.
    */
  def toSQLArray(uuids: Seq[UUID[_]]): String = {
    "array[" + uuids.map(u => s"'$u'").mkString(",") + "]::uuid[]"
  }

  // Type class instances
  implicit val writes: Writes[UUID[_]] = Writes(uuid =>
    if (uuid == null) JsNull else JsString(uuid.toString)
  )

  implicit def reads[A]: Reads[UUID[A]] = Reads.uuidReads.map(UUID.fromRaw)

  implicit def columnInstance[A]: Column[UUID[A]] = Column.nonNull1 { (value, _) =>
    value match {
      case s: String => UUID.fromString[A](s).left.map(TypeDoesNotMatch)
      case b: Array[Byte] => fromByteArrayRaw(b).right.map(UUID.fromRaw[A]).left.map(TypeDoesNotMatch)
      case u: UUID.Raw => Right(UUID.fromRaw[A](u))
      case _ => Left(TypeDoesNotMatch(s"Could not convert from ${value.getClass.getName} to UUID ($value)"))
    }
  }

  implicit def toStatementInstance[A]: ToStatement[UUID[A]] = new ToStatement[UUID[A]] {
    def set(s: PreparedStatement, index: Int, aValue: UUID[A]) {
      s.setObject(index, UUID.toRaw[A](aValue))
    }
  }
}