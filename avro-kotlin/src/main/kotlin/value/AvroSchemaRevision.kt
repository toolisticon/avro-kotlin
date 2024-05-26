package io.toolisticon.kotlin.avro.value


/**
 * The version of a schema.
 *
 * Avro internally does not have the concept of a revision, but it is useful for schema evolution
 * and data migration (aka upcasting) to add an optional version hint.
 */
@JvmInline
value class AvroSchemaRevision(override val value: String) : ValueType<String> {
}
