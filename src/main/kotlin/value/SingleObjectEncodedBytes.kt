package io.toolisticon.avro.kotlin.value

import java.nio.ByteOrder

/**
 * Message encoded as [Single Object](https://avro.apache.org/docs/current/spec.html#single_object_encoding) ByteArray.
 */
class SingleObjectEncodedBytes private constructor(
  private val bytes: ByteArrayValue,
  val fingerprint: AvroFingerprint,
  /**
   * The encoded message. This is only the payload data,
   * so no marker header and encoded schemaId are present.
   */
  val payload: ByteArrayValue
) : ValueType<ByteArray> by bytes, WithHexString {

  companion object {
    /**
     * Marker bytes according to Avro schema specification v1.
     */
    val AVRO_V1_HEADER = byteArrayOf(-61, 1) // [C3 01]

    /**
     * Length of default avro header bytes.
     */
    val AVRO_HEADER_LENGTH = AVRO_V1_HEADER.size + Long.SIZE_BYTES

    private fun create(bytes: ByteArray): SingleObjectEncodedBytes {
      val value = ByteArrayValue(bytes)
      val (marker, fingerprint, payload) = value.split(AVRO_V1_HEADER.size, AVRO_HEADER_LENGTH)

      require(marker.contentEquals(ByteArrayValue(AVRO_V1_HEADER))) { "SingleObjectEncoded must start with marker bytes." }
      require(fingerprint.size == Long.SIZE_BYTES) { "Fingerprint must be exactly Long.SIZE_BYTES (${Long.SIZE_BYTES}." }

      return SingleObjectEncodedBytes(
        bytes = value,
        fingerprint = AvroFingerprint(value.buffer.order(ByteOrder.LITTLE_ENDIAN).long),
        payload = payload
      )
    }
  }

  private constructor(other: SingleObjectEncodedBytes) : this(bytes = other.bytes, fingerprint = other.fingerprint, payload = other.payload)

  constructor(bytes: ByteArray) : this(create(bytes))

  override val hex: HexString get() = bytes.hex
}
