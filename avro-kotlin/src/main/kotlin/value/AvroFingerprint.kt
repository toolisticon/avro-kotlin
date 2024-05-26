package io.toolisticon.kotlin.avro.value

import _ktx.StringKtx.trimToNull
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes.Companion.verifyAvroHeader
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.apache.avro.message.BadHeaderException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The schema fingerprint as defined by [SchemaNormalization.parsingFingerprint64].
 *
 * In the Avro context, fingerprints of Parsing Canonical Form can be useful in a number of applications;
 * for example, to cache encoder and decoder objects, to tag data items with a short substitute for the
 * writer’s full schema, and to quickly negotiate common-case schemas between readers and writers.
 */
@JvmInline
value class AvroFingerprint(override val value: Long) : Comparable<Long> by value, ValueType<Long> {
  companion object {
    val NULL = AvroFingerprint(0L)

    fun parsingFingerprint64(schema: Schema) = SchemaNormalization.parsingFingerprint64(schema)
    fun fingerprint64(bytes: ByteArray) = SchemaNormalization.fingerprint64(bytes)

    fun Iterable<AvroFingerprint>.sum() = fold(NULL) { acc, avroFingerprint -> acc + avroFingerprint }
    fun Long.toBytes(): ByteArrayValue = ByteArrayValue(ByteBuffer.allocate(Long.SIZE_BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(this))
    fun ByteArrayValue.readLong(index: Int = 0): Long = buffer.order(ByteOrder.LITTLE_ENDIAN).getLong(index)

    fun ofNullable(string: String?): AvroFingerprint = string?.trimToNull()?.let {
      AvroFingerprint(fingerprint64(it.encodeToByteArray()))
    } ?: NULL


    fun of(field: Schema.Field) = of(field.schema())
    fun of(schema: Schema) = AvroFingerprint(parsingFingerprint64(schema))

    fun of(message: Protocol.Message) = listOf(
      ofNullable(message.name),
      of(message.errors),
      of(message.request),
      of(message.response)
    ).sum()

    /**
     * Extracts the writer-schema fingerprint from given [bytes].
     *
     * @param bytes assumed to be a valid avro single object encoded byte array
     * @throws BadHeaderException if bytes is not a valid single object encoded byte array
     */
    @Throws(BadHeaderException::class)
    fun of(bytes: ByteArrayValue) = AvroFingerprint(
      verifyAvroHeader(bytes).readLong(index = 2)
    )
  }

  operator fun plus(other: AvroFingerprint): AvroFingerprint = AvroFingerprint(this.value + other.value)
  operator fun plus(others: List<AvroFingerprint>): AvroFingerprint = (others + this).sum()

  val hex: HexString get() = byteValue.hex
  val byteValue: ByteArrayValue get() = value.toBytes()

  override fun toString() = hex.value
}
