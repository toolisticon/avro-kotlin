package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Generic wrapper around a [ByteArray]. This can be used without specifying
 * a dedicated purpose like [SingleObjectEncodedBytes] or [BinaryEncodedBytes].
 */
@JvmInline
value class ByteArrayValue(override val value: ByteArray) : ByteArrayValueType {
  constructor(hex: String) : this(HexString(hex))
  constructor(hex: HexString) : this(hex.byteArray.value)
  constructor(buffer: ByteBuffer) : this(buffer.array())

  constructor(writeToBaos: ByteArrayOutputStream.() -> Unit) : this(value = ByteArrayOutputStream().use { baos ->
    baos.writeToBaos()
    baos.toByteArray()
  })

  /**
   * Converts a byte array into its hexadecimal string representation
   * e.g. for the V1_HEADER => [C3 01]
   * @return the hexadecimal string representation of the input byte array
   */
  val formatted: String get() = hex.formatted

  fun contentEquals(other: ByteArrayValue) = this.value.contentEquals(other.value)

  override fun toString() = hex.formatted

  fun toUtf8String() = value.toString(AvroKotlin.UTF_8)
}
