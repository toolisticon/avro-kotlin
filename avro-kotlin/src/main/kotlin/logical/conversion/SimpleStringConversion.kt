package io.toolisticon.avro.kotlin.logical.conversion

import _ktx.StringKtx
import io.toolisticon.avro.kotlin.logical.BooleanLogicalType
import io.toolisticon.avro.kotlin.logical.StringLogicalType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.LogicalType

abstract class SimpleStringConversion<T : StringLogicalType, CONVERTED_TYPE>(
  val logicalType: T,
  convertedType: Class<CONVERTED_TYPE>
) : StringConversion<CONVERTED_TYPE>(logicalTypeName = logicalType.name, convertedType = convertedType) {
  private val defaultSchema = logicalType.schema()

  final override fun fromAvro(value: String, schema: AvroSchema, logicalType: LogicalType?): CONVERTED_TYPE {
    return fromAvro(value)
  }

  override fun toAvro(value: CONVERTED_TYPE, schema: AvroSchema, logicalType: LogicalType?): String {
    return toAvro(value)
  }

  abstract fun fromAvro(value: String): CONVERTED_TYPE

  abstract fun toAvro(value: CONVERTED_TYPE): String

  override fun toString(): String = StringKtx.toString(this::class.java.simpleName) {
    add(property = "name", value = logicalTypeName, wrap = "'")
    add("convertedType", convertedType.canonicalName)
    add("type", logicalType.type)
  }


}

abstract class SimpleBooleanConversion<T : BooleanLogicalType, CONVERTED_TYPE>(
  val logicalType: T,
  convertedType: Class<CONVERTED_TYPE>
) : BooleanConversion<CONVERTED_TYPE>(logicalType.name, convertedType) {

  final override fun fromAvro(value: Boolean, schema: AvroSchema, logicalType: LogicalType?): CONVERTED_TYPE {
    return fromAvro(value)
  }

  final override fun toAvro(value: CONVERTED_TYPE, schema: AvroSchema, logicalType: LogicalType?): Boolean {
    return toAvro(value)
  }

  abstract fun fromAvro(value: Boolean): CONVERTED_TYPE
  abstract fun toAvro(value: CONVERTED_TYPE): Boolean


  override fun toString(): String = StringKtx.toString(this::class.java.simpleName) {
    add(property = "name", value = logicalTypeName, wrap = "'")
    add("convertedType", convertedType.canonicalName)
    add("type", logicalType.type)
  }

}
