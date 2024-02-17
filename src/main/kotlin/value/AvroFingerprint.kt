package io.toolisticon.avro.kotlin.value

import _ktx.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaField
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes.Companion.verifyAvroHeader
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


  }

  constructor(schema: AvroSchema) : this(schema.fingerprint.value)
  constructor(field: AvroSchemaField) : this(field.schema)

  /**
   * Extracts the writer-schema fingerprint from given [bytes].
   *
   * @param bytes assumed to be a valid avro single object encoded byte array
   * @throws BadHeaderException if bytes is not a valid single object encoded byte array
   */
  @Throws(BadHeaderException::class)
  constructor(bytes: ByteArrayValue) : this(
    verifyAvroHeader(bytes).readLong(index = 2)
  )

  constructor(field: Schema.Field) : this(field.schema())
  constructor(schema: Schema) : this(parsingFingerprint64(schema))
  constructor(string: String?) : this(string?.trimToNull()?.let { fingerprint64(it.encodeToByteArray()) } ?: NULL.value)

  constructor(message: Protocol.Message) : this(
    listOf(
      AvroFingerprint(message.name),
      AvroFingerprint(message.errors),
      AvroFingerprint(message.request),
      AvroFingerprint(message.response)
    ).sum().value
  )

  operator fun plus(other: AvroFingerprint): AvroFingerprint = AvroFingerprint(this.value + other.value)
  operator fun plus(others: List<AvroFingerprint>): AvroFingerprint = (others + this).sum()

  val hex: HexString get() = byteValue.hex
  val byteValue: ByteArrayValue get() = value.toBytes()

  override fun toString() = hex.value
}
