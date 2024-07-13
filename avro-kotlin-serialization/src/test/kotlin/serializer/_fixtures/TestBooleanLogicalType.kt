package io.toolisticon.kotlin.avro.serialization.serializer._fixtures

import io.toolisticon.kotlin.avro.logical.BooleanLogicalType
import io.toolisticon.kotlin.avro.logical.BooleanLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.BooleanLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.BooleanLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.serializer.BooleanLogicalTypeSerializerTest.MyBooleanType
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object TestBooleanLogicalType : BooleanLogicalType("boolean-type".toLogicalTypeName()) {

  @Suppress("unused")
  class LogicalTypeFactory : BooleanLogicalTypeFactory<TestBooleanLogicalType>(logicalType = TestBooleanLogicalType)

  class TypeConversion : BooleanLogicalTypeConversion<TestBooleanLogicalType, MyBooleanType>(TestBooleanLogicalType, MyBooleanType::class) {
    override fun fromAvro(value: Boolean): MyBooleanType = MyBooleanType(value)
    override fun toAvro(value: MyBooleanType): Boolean = value.value
  }

  class TypeSerializer : BooleanLogicalTypeSerializer<TestBooleanLogicalType, MyBooleanType>(TypeConversion())

  class TypeSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(MyBooleanType::class, TypeSerializer())
    }
  }
}
