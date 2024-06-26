package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.AvroKotlin
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Generic wrapper around a [ByteArray]. This can be used without specifying
 * a dedicated purpose like [SingleObjectEncodedBytes] or [BinaryEncodedBytes].
 */
@JvmInline
value class ByteArrayValue(override val value: ByteArray) : ByteArrayValueType {

  companion object {
    fun parse(hex: String) = parse(HexString.parse(hex))
    fun parse(hex: HexString) = ByteArrayValue(hex.byteArray.value)

  }


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
