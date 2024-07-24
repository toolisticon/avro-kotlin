package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.compatibleToReadFrom
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityResult
import org.apache.avro.SchemaCompatibility.SchemaPairCompatibility
import java.util.concurrent.ConcurrentHashMap

/**
 * Used as a key in [AvroSchemaCompatibilityMap] to cache [AvroSchemaCompatibility] results.
 */
@JvmInline
value class AvroFingerprintPair private constructor(override val value: Pair<AvroFingerprint, AvroFingerprint>) : PairType<AvroFingerprint, AvroFingerprint> {
  companion object {
    fun of(writerSchema: AvroSchema, readerSchema: AvroSchema) = AvroFingerprintPair(writerSchema.fingerprint, readerSchema.fingerprint)
  }

  constructor(writer: AvroFingerprint, reader: AvroFingerprint) : this(writer to reader)

  val writerSchema: AvroFingerprint get() = value.first
  val readerSchema: AvroFingerprint get() = value.second
}

/**
 * Wraps apache-avro [SchemaPairCompatibility] and allows simple(typesafe access to
 * helper functions and derived attributes.
 */
@JvmInline
value class AvroSchemaCompatibility(override val value: SchemaPairCompatibility) : ValueType<SchemaPairCompatibility> {
  val result: SchemaCompatibilityResult get() = value.result

  val isCompatible: Boolean get() = result.incompatibilities.isEmpty()
}

/**
 * Mutable cache for [AvroSchemaCompatibility], so based on a writer- and reader-schema
 * fingerprint, we only calculate once.
 */
@JvmInline
value class AvroSchemaCompatibilityMap(
  private val value: MutableMap<AvroFingerprintPair, AvroSchemaCompatibility> = ConcurrentHashMap()
) {

  fun compatibleToReadFrom(writerSchema: AvroSchema, readerSchema: AvroSchema): AvroSchemaCompatibility {
    val key = AvroFingerprintPair.of(writerSchema, readerSchema)

    return value.computeIfAbsent(key) { _ -> readerSchema.compatibleToReadFrom(writerSchema) }
  }

  fun isCompatible(writerSchema: AvroFingerprint, readerSchema: AvroFingerprint): Boolean =
    value.getOrDefault(AvroFingerprintPair(writerSchema, readerSchema), null)
      ?.isCompatible
      ?: false
}
