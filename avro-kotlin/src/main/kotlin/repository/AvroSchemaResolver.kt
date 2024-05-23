package io.toolisticon.avro.kotlin.repository

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import org.apache.avro.Schema
import org.apache.avro.message.MissingSchemaException
import org.apache.avro.message.SchemaStore

/**
 * Supply an [AvroSchema] by [AvroFingerprint].
 */
fun interface AvroSchemaResolver : SchemaStore {


  @Throws(MissingSchemaException::class)
  operator fun get(fingerprint: AvroFingerprint): AvroSchema

  @Throws(MissingSchemaException::class)
  operator fun invoke(): AvroSchema = this[AvroFingerprint.NULL]

  @Throws(MissingSchemaException::class)
  override fun findByFingerprint(fingerprint: Long): Schema = this[AvroFingerprint(fingerprint)].get()
}
