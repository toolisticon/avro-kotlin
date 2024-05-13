package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion

data class LogicalTypeConversions(
  private val conversions: Map<LogicalTypeName, Conversion<*>> = emptyMap()
) : List<Conversion<*>> by conversions.values.toList() {




}
