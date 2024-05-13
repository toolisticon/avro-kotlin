package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.logical.conversion.SimpleBooleanConversion
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName

enum class DummyBooleanEnum(val value: Boolean) {
  TRUE(true),FALSE(false);
}

object DummyBooleanSimpleLogicalType : BooleanSimpleLogicalType(name = "boolean".toLogicalTypeName())

class DummyBooleanSimpleLogicalTypeFactory : BooleanSimpleLogicalTypeFactory<DummyBooleanSimpleLogicalType>(DummyBooleanSimpleLogicalType)

class DummyBooleanConversion : SimpleBooleanConversion<DummyBooleanSimpleLogicalType,DummyBooleanEnum>(DummyBooleanSimpleLogicalType,DummyBooleanEnum::class.java) {
  override fun fromAvro(value: Boolean): DummyBooleanEnum = if (value) DummyBooleanEnum.TRUE else DummyBooleanEnum.FALSE

  override fun toAvro(value: DummyBooleanEnum): Boolean = value.value
}

internal class BooleanLogicalTypeTest {

}
