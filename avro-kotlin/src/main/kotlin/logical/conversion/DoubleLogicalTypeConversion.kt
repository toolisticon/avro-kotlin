package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.DoubleLogicalType
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass

abstract class DoubleLogicalTypeConversion<LOGICAL : DoubleLogicalType, CONVERTED_TYPE : Any>(
  logicalType: LOGICAL,
  convertedType: KClass<CONVERTED_TYPE>
) : AvroLogicalTypeConversion<LOGICAL, Double, CONVERTED_TYPE>(
  logicalType = logicalType,
  convertedType = convertedType
) {

  // called by avro internally
  override fun fromDouble(value: Double, schema: Schema?, type: LogicalType?): CONVERTED_TYPE {
    validateSchemaAndLogicalType(schema, type)
    return fromAvro(value)
  }

  // called by avro internally
  override fun toDouble(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?): Double {
    validateSchemaAndLogicalType(schema, type)
    return toAvro(value)
  }
}
