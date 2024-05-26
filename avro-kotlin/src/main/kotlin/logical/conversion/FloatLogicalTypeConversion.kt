package io.toolisticon.kotlin.avro.logical.conversion

import io.toolisticon.kotlin.avro.logical.FloatLogicalType
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass

abstract class FloatLogicalTypeConversion<LOGICAL : FloatLogicalType, CONVERTED_TYPE : Any>(
  logicalType: LOGICAL,
  convertedType: KClass<CONVERTED_TYPE>
) : AvroLogicalTypeConversion<LOGICAL, Float, CONVERTED_TYPE>(
  logicalType = logicalType,
  convertedType = convertedType
) {

  // called by avro internally
  override fun fromFloat(value: Float, schema: Schema?, type: LogicalType?): CONVERTED_TYPE {
    validateSchemaAndLogicalType(schema, type)
    return fromAvro(value)
  }

  // called by avro internally
  override fun toFloat(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?): Float {
    validateSchemaAndLogicalType(schema, type)
    return toAvro(value)
  }
}
