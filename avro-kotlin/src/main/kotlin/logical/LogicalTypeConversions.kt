package io.toolisticon.kotlin.avro.logical

import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.apache.avro.Conversion

data class LogicalTypeConversions(
  private val conversions: Map<LogicalTypeName, Conversion<*>> = emptyMap()
) : List<Conversion<*>> by conversions.values.toList() {




}
