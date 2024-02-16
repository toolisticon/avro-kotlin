package io.toolisticon.avro.kotlin.value

import org.apache.avro.message.BadHeaderException
import org.apache.avro.message.BinaryMessageEncoder


/**
 * Marker bytes according to Avro schema specification v1.
 *
 * Derived via reflection from hidden constant in [org.apache.avro.message.BinaryMessageEncoder].
 */
object AvroHeaderBytes : ByteArrayValueType {

  override val value: ByteArray = BinaryMessageEncoder::class.java.getDeclaredField("V1_HEADER")
    .apply { isAccessible = true }
    .get(null) as ByteArray

  @Throws(BadHeaderException::class)
  fun verify(bytes: ByteArrayValue): ByteArrayValue = bytes.apply {
    if (SingleObjectEncodedBytes.AVRO_V1_HEADER[0] != bytes[0] || SingleObjectEncodedBytes.AVRO_V1_HEADER[1] != bytes[1]) {
      throw BadHeaderException("Unrecognized header bytes: [0x%02X 0x%02X]".format(bytes[0], bytes[1]))
    }
  }

  override fun toString() = hex.formatted
}
