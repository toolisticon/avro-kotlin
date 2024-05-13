package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion
import org.apache.avro.LogicalTypes
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificData

class AvroLogicalTypes(
  private val conversions: Map<LogicalTypeName, Conversion<*>>
) {
  companion object {
    fun loadConversions(): List<AvroLogicalType<*>> = LogicalTypes.getCustomRegisteredTypes().values
      .filterIsInstance<AvroLogicalType<*>>()
      .toList()
  }

  /**
   * Find registered custom logical types, including conversions.
   */
  constructor() : this(conversions = buildMap {
    putAll(BuiltInLogicalType.CONVERSIONS_BY_NAME)
    putAll(loadConversions().associate { it.name to it.conversion })
  })

  val genericData: GenericData by lazy {
    GenericData().apply(addLogicalTypeConversions)
  }

  val specificData: SpecificData by lazy {
    SpecificData().apply(addLogicalTypeConversions)
  }

  private val addLogicalTypeConversions: GenericData.() -> Unit = {
    this@AvroLogicalTypes.conversions.values.forEach { addLogicalTypeConversion(it) }
  }
}
