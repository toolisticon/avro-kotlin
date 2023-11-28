package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.model.AvroSchema
import io.toolisticon.avro.kotlin.model.AvroSchemaField
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization

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
    fun Iterable<AvroFingerprint>.sum() = fold(NULL) { acc, avroFingerprint -> acc + avroFingerprint }
  }

  constructor(schema: AvroSchema) : this(schema.fingerprint.value)
  constructor(field: AvroSchemaField) : this(field.schema)

  constructor(field: Schema.Field) : this(field.schema())
  constructor(schema: Schema) : this(SchemaNormalization.parsingFingerprint64(schema))
  constructor(bytes: ByteArray) : this(SchemaNormalization.fingerprint64(bytes))
  constructor(string: String?) : this(string?.trimToNull()?.let { AvroFingerprint(it.encodeToByteArray()) }?.value ?: NULL.value)

  constructor(protocol: Protocol) : this(
    buildList<AvroFingerprint> {
      add(AvroFingerprint(protocol.namespace))
      add(AvroFingerprint(protocol.name))
      addAll(protocol.types.map { AvroFingerprint(it) })
      addAll(protocol.messages.values.map { AvroFingerprint(it) })
    }.sum().value
  )

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

  val hex: HexString get() = HexString(value)

  override fun toString() = hex.value
}
