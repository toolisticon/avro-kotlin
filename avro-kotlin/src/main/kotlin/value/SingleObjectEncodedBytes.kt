package io.toolisticon.kotlin.avro.value

import org.apache.avro.message.BadHeaderException
import java.nio.ByteBuffer

/**
 * Message encoded as [Single Object](https://avro.apache.org/docs/current/spec.html#single_object_encoding) ByteArray.
 */
class SingleObjectEncodedBytes private constructor(
  /**
   * The complete single object encoded bytes including marker bytes, fingerprint and payload.
   */
  override val value: ByteArray
) : ByteArrayValueType {

  companion object {

    /**
     * Marker bytes according to Avro schema specification v1.
     */
    val AVRO_V1_HEADER = AvroHeaderBytes

    /**
     * Length of default avro header bytes.
     */
    val AVRO_HEADER_LENGTH = AvroHeaderBytes.size + Long.SIZE_BYTES

    /**
     * @param The byteArray to verify.
     * @return the same byteArray for fluent usage
     * @throws BadHeaderException if byteArray does to start with V1_HEADER or is too short to contain a fingerprint.
     */
    @Throws(BadHeaderException::class)
    fun verifyAvroHeader(bytes: ByteArrayValue): ByteArrayValue = AvroHeaderBytes.verify(bytes.apply {
      if (bytes.size <= AVRO_HEADER_LENGTH) {
        throw BadHeaderException("Header is too short(min=$AVRO_HEADER_LENGTH) for 8-byte fingerprint: ${bytes.hex.formatted}")
      }
    })

    fun of(bytes: ByteArrayValue) = SingleObjectEncodedBytes(bytes.value)

    fun of(bytes: ByteBuffer) = of(ByteArrayValue(bytes))

    fun parse(hex: HexString) = of(hex.byteArray)

    fun of(bytes: ByteArray) = of(ByteArrayValue(bytes))
  }

  constructor(fingerprint: AvroFingerprint, payload: BinaryEncodedBytes) : this(value = AvroHeaderBytes.value + fingerprint.byteValue.value + payload.value)

  override val hex: HexString by lazy { HexString.of(value) }

  /**
   * Memoized for access.
   * @return The schema fingerprint (as included in value(2,10)).
   */
  val fingerprint: AvroFingerprint by lazy { AvroFingerprint.of(bytes = ByteArrayValue(value)) }

  /**
   * The message payload, binary encoded (value(11,end)).
   * @return copy of relevant bytes
   */
  val payload: BinaryEncodedBytes get() = BinaryEncodedBytes(value.copyOfRange(AVRO_HEADER_LENGTH, value.size))

  override fun toString() = hex.formatted
}
