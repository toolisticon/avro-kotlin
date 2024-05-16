package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.BooleanLogicalType
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass

abstract class BooleanLogicalTypeConversion<LOGICAL : BooleanLogicalType, CONVERTED_TYPE : Any>(
  logicalType: LOGICAL,
  convertedType: KClass<CONVERTED_TYPE>
) : AvroLogicalTypeConversion<LOGICAL, Boolean, CONVERTED_TYPE>(
  logicalType = logicalType,
  convertedType = convertedType
) {

  // called by avro internally
  override fun fromBoolean(value: Boolean, schema: Schema?, type: LogicalType?): CONVERTED_TYPE {
    validateSchemaAndLogicalType(schema, type)
    return fromAvro(value)
  }

  // called by avro internally
  override fun toBoolean(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?): Boolean {
    validateSchemaAndLogicalType(schema, type)
    return toAvro(value)
  }
}
