package io.toolisticon.avro.kotlin

import org.apache.avro.LogicalTypes.LogicalTypeFactory

interface AvroLogicalType {

  val factory: LogicalTypeFactory

}
