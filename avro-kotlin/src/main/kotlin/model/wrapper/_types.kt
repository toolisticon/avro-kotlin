package io.toolisticon.kotlin.avro.model.wrapper

import org.apache.avro.SchemaFormatter

interface AvroSchemaFormatter : SchemaFormatter {

  fun format(schema: AvroSchema): String = format(schema.get())

}
