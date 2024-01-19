package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import org.apache.avro.message.SchemaStore

interface AvroSchemaStore {

  operator fun get(fingerprint: AvroFingerprint): AvroSchema

  val schemaStore: SchemaStore get() = SchemaStore { fingerprint -> get(AvroFingerprint(fingerprint)).get() }
}
