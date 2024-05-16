package io.toolisticon.avro.kotlin.logical.conversion

import _ktx.StringKtx
import io.toolisticon.avro.kotlin.logical.AvroLogicalType
import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass


sealed class AvroLogicalTypeConversion<LOGICAL : AvroLogicalType<JVM_TYPE>, JVM_TYPE : Any, CONVERTED_TYPE : Any>(
  val logicalType: LOGICAL,
  val convertedType: KClass<CONVERTED_TYPE>
) : TypeConverter<JVM_TYPE, CONVERTED_TYPE>, Conversion<CONVERTED_TYPE>() {
  protected val recommendedSchema = logicalType.schema()

  protected fun validateSchemaAndLogicalType(schema: Schema?, type: LogicalType?) {
    require(schema == null || schema.type == getRecommendedSchema().type) {
      "Conversion for $logicalType can not be applied to Schema.Type=${schema?.type}."
    }
    require(type == null || type.name == logicalTypeName) {
      "Conversion for $logicalType can not be applied to logicalTypeName=${type?.name}."
    }
  }

  override fun getConvertedType(): Class<CONVERTED_TYPE> = convertedType.java

  override fun getLogicalTypeName(): String = logicalType.getName()

  /**
   * The [Schema] including the relevant [org.apache.avro.LogicalType].
   */
  override fun getRecommendedSchema(): Schema = recommendedSchema.get()

  override fun toString(): String = StringKtx.toString(this::class.java.simpleName) {
    add(property = "logicalType", value = logicalType.name, wrap = "'")
    add(property = "schemaType", value = logicalType.type)
    add("convertedType", convertedType.qualifiedName ?: "N/A")
  }
}
