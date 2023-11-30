package io.toolisticon.avro.kotlin._test

import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema

class CustomLogicalTypeFactory : LogicalTypes.LogicalTypeFactory {
  companion object {
    const val NAME = "custom"
  }

  data object CustomLogicalType : LogicalType(NAME) {

    override fun addToSchema(schema: Schema): Schema {
      return super.addToSchema(schema)
    }

    override fun validate(schema: Schema?) {
      super.validate(schema)
    }
  }

  class CustomLogicalTypeConversion : Conversion<String>() {
    override fun getConvertedType(): Class<String> {
      return String::class.java
    }

    override fun getLogicalTypeName(): String {
      return NAME
    }
  }

  override fun getTypeName(): String {
    return NAME
  }

  override fun fromSchema(schema: Schema): LogicalType {
    return CustomLogicalType
  }


}
