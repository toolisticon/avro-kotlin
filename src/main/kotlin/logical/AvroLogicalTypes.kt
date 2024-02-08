package io.toolisticon.avro.kotlin.logical

import org.apache.avro.LogicalTypes

object AvroLogicalTypes {

  /**
   * Find registered custom logical types, including conversions.
   */
  val avroLogicalTypes by lazy {
    LogicalTypes.getCustomRegisteredTypes().values
      .filterIsInstance<AvroLogicalType<*>>()
      .toList()
  }

}
