package io.toolisticon.kotlin.avro.serialization._test

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import kotlinx.serialization.Serializable
import org.apache.avro.SchemaBuilder

/**
 * It cannot get much simpler than this ...
 */
@Serializable
data class BarString(val name: String)

val barStringSchema = AvroSchema(
  SchemaBuilder.record("BarString")
    .namespace("io.toolisticon.kotlin.avro.serialization._test")
    .fields()
    .requiredString("name")
    .endRecord()
)

