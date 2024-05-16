package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.LongLogicalType
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass

abstract class LongLogicalTypeConversion<LOGICAL : LongLogicalType, CONVERTED_TYPE : Any>(
  logicalType: LOGICAL,
  convertedType: KClass<CONVERTED_TYPE>
) : AvroLogicalTypeConversion<LOGICAL, Long, CONVERTED_TYPE>(
  logicalType = logicalType,
  convertedType = convertedType
) {

  // called by avro internally
  override fun fromLong(value: Long, schema: Schema?, type: LogicalType?): CONVERTED_TYPE {
    validateSchemaAndLogicalType(schema, type)
    return fromAvro(value)
  }

  // called by avro internally
  override fun toLong(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?): Long {
    validateSchemaAndLogicalType(schema, type)
    return toAvro(value)
  }
}
