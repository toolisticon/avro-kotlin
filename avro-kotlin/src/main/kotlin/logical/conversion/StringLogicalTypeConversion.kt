package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.StringLogicalType
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass

abstract class StringLogicalTypeConversion<LOGICAL : StringLogicalType, CONVERTED_TYPE : Any>(
  logicalType: LOGICAL,
  convertedType: KClass<CONVERTED_TYPE>
) : AvroLogicalTypeConversion<LOGICAL, String, CONVERTED_TYPE>(
  logicalType = logicalType,
  convertedType = convertedType
) {

  // called by avro internally
  override fun fromCharSequence(value: CharSequence, schema: Schema?, type: LogicalType?): CONVERTED_TYPE {
    validateSchemaAndLogicalType(schema, type)
    return fromAvro(value.toString())
  }

  // called by avro internally
  override fun toCharSequence(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?): CharSequence {
    validateSchemaAndLogicalType(schema, type)
    return toAvro(value)
  }
}
