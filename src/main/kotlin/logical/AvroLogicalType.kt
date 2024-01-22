package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes.LogicalTypeFactory

interface AvroLogicalType<T> : LogicalTypeFactory {

  override fun getTypeName(): String = name.value

  val name: LogicalTypeName

  val logicalType: LogicalType

  val conversion: Conversion<T>
}
