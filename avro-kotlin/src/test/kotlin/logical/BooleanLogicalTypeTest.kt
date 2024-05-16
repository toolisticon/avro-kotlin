package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.logical.conversion.BooleanLogicalTypeConversion
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName

internal class BooleanLogicalTypeTest {

  enum class DummyBooleanEnum(val value: Boolean) {
    TRUE(true), FALSE(false);
  }

  object DummyBooleanLogicalType : BooleanLogicalType(name = "boolean".toLogicalTypeName())

  class DummyBooleanLogicalTypeFactory : BooleanLogicalTypeFactory<DummyBooleanLogicalType>(DummyBooleanLogicalType)

  class DummyBooleanConversion :
    BooleanLogicalTypeConversion<DummyBooleanLogicalType, DummyBooleanEnum>(DummyBooleanLogicalType, DummyBooleanEnum::class) {
    override fun fromAvro(value: Boolean): DummyBooleanEnum = if (value) DummyBooleanEnum.TRUE else DummyBooleanEnum.FALSE

    override fun toAvro(value: DummyBooleanEnum): Boolean = value.value
  }


}
