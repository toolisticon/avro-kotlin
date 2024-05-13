package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.SimpleLogicalType
import io.toolisticon.avro.kotlin.logical.StringSimpleLogicalType
import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.Schema


sealed class SimpleLogicalTypeConversion<LOGICAL : SimpleLogicalType<REPRESENTATION, CONVERSION>, REPRESENTATION : Any, CONVERSION : Any, CONVERTED_TYPE : Any>(
  val logicalType: LOGICAL,
  private val convertedType: Class<CONVERTED_TYPE>
) : TypeConverter<REPRESENTATION, CONVERTED_TYPE>, Conversion<CONVERTED_TYPE>() {
  protected val recommendedSchema = logicalType.schema()

  protected fun validateSchemaAndLogicalType(schema: Schema?, type: LogicalType?) {
    require(schema == null || schema.type == getRecommendedSchema().type) {
      "Conversion for $logicalType can not be applied to Schema.Type=${schema?.type}."
    }
    require(type == null || type.name == logicalTypeName) {
      "Conversion for $logicalType can not be applied to logicalTypeName=${type?.name}."
    }
  }

  override fun getConvertedType(): Class<CONVERTED_TYPE> = convertedType

  override fun getLogicalTypeName(): String = logicalType.getName()

  /**
   * The [Schema] including the relevant [org.apache.avro.LogicalType].
   */
  override fun getRecommendedSchema(): Schema = recommendedSchema.get()
}

abstract class StringSimpleLogicalTypeConversion<LOGICAL : StringSimpleLogicalType, CONVERTED_TYPE : Any>(
  logicalType: LOGICAL,
  convertedType: Class<CONVERTED_TYPE>
) : SimpleLogicalTypeConversion<LOGICAL, String, CharSequence, CONVERTED_TYPE>(
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
