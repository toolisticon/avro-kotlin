package io.toolisticon.avro.kotlin.logical

import org.apache.avro.LogicalTypes.LogicalTypeFactory

interface AvroLogicalType {

  val factory: LogicalTypeFactory

}
