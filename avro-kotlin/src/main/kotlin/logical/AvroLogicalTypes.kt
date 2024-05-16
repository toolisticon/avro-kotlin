package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion
import org.apache.avro.LogicalTypes
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificData

@Deprecated("remove")
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
    //putAll(loadConversions().associate { it.name to it.conversion })
  })

  val genericData: GenericData by lazy {
    AvroKotlin.genericData.apply(addLogicalTypeConversions)
  }

  val specificData: SpecificData by lazy {
    AvroKotlin.specificData.apply(addLogicalTypeConversions)
  }

  private val addLogicalTypeConversions: GenericData.() -> Unit = {
    this@AvroLogicalTypes.conversions.values.forEach { addLogicalTypeConversion(it) }
  }
}
