package io.toolisticon.kotlin.avro.value

import org.apache.avro.message.BadHeaderException
import java.nio.ByteBuffer

/**
 * Message encoded as [Single Object](https://avro.apache.org/docs/current/spec.html#single_object_encoding) ByteArray.
 */
@JvmInline
value class SingleObjectEncodedBytes private constructor(
  private val pair: Pair<AvroFingerprint, BinaryEncodedBytes>
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
     * @param the byteArray to verify
     * @return the same byteArray for fluent usage
     * @throws BadHeaderException if byteArray does to start with V1_HEADER or is too short to contain a fingerprint.
     */
    @Throws(BadHeaderException::class)
    fun verifyAvroHeader(bytes: ByteArrayValue): ByteArrayValue = AvroHeaderBytes.verify(bytes.apply {
      if (bytes.size <= AVRO_HEADER_LENGTH) {
        throw BadHeaderException("Header is too short(min=$AVRO_HEADER_LENGTH) for 8-byte fingerprint: ${bytes.hex.formatted}")
      }
    })

    fun of(bytes: ByteArrayValue) = SingleObjectEncodedBytes(
      fingerprint = AvroFingerprint.of(bytes),
      payload = BinaryEncodedBytes(bytes.value.copyOfRange(AVRO_HEADER_LENGTH, bytes.value.size))
    )

    fun of(bytes: ByteBuffer) = of(ByteArrayValue(bytes))

    fun parse(hex: HexString) = of(hex.byteArray)

    fun of(bytes: ByteArray) = of(ByteArrayValue(bytes))
  }

  constructor(fingerprint: AvroFingerprint, payload: BinaryEncodedBytes) : this(fingerprint to payload)

  override val value: ByteArray get() = AvroHeaderBytes.value + fingerprint.byteValue.value + payload.value

  override val hex: HexString
    get() = super.hex
  override val buffer: ByteBuffer
    get() = super.buffer
  override val size: Int
    get() = super.size

  override fun get(index: Int): Byte {
    return super.get(index)
  }

  /**
   * The [AvroFingerprint] of this encoded byte array.
   */
  val fingerprint: AvroFingerprint get() = pair.first

  /**
   * The payload of this encoded byte array.
   */
  val payload: BinaryEncodedBytes get() = pair.second


  override fun toString() = hex.formatted
}
