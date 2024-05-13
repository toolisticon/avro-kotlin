package io.toolisticon.avro.kotlin.example.money

import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes.LogicalTypeFactory
import org.apache.avro.Schema

class MoneyLogicalTypeFactory : LogicalTypeFactory {

  override fun fromSchema(schema: Schema): LogicalType? = if (schema.getProp(LogicalType.LOGICAL_TYPE_PROP) == typeName) {
    MoneyLogicalType
  } else null

  override fun getTypeName(): String = LOGICAL_TYPE_NAME.value
}
