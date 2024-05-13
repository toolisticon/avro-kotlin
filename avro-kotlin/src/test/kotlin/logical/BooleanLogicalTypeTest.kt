package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.logical.conversion.SimpleBooleanConversion
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName

enum class DummyBooleanEnum(val value: Boolean) {
  TRUE(true),FALSE(false);
}

object DummyBooleanLogicalType : BooleanLogicalType(name = "boolean".toLogicalTypeName())

class DummyBooleanLogicalTypeFactory : BooleanLogicalTypeFactory<DummyBooleanLogicalType>(DummyBooleanLogicalType)

class DummyBooleanConversion : SimpleBooleanConversion<DummyBooleanLogicalType,DummyBooleanEnum>(DummyBooleanLogicalType,DummyBooleanEnum::class.java) {
  override fun fromAvro(value: Boolean): DummyBooleanEnum = if (value) DummyBooleanEnum.TRUE else DummyBooleanEnum.FALSE

  override fun toAvro(value: DummyBooleanEnum): Boolean = value.value
}

internal class BooleanLogicalTypeTest {

}
