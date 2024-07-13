package io.toolisticon.kotlin.avro.serialization.serializer._fixtures

import io.toolisticon.kotlin.avro.logical.IntLogicalType
import io.toolisticon.kotlin.avro.logical.IntLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.IntLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.IntLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.serializer.IntLogicalTypeSerializerTest.MyIntType
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object TestIntLogicalType : IntLogicalType("int-type".toLogicalTypeName()) {

  @Suppress("unused")
  class LogicalTypeFactory : IntLogicalTypeFactory<TestIntLogicalType>(logicalType = TestIntLogicalType)

  class TypeConversion : IntLogicalTypeConversion<TestIntLogicalType, MyIntType>(TestIntLogicalType, MyIntType::class) {
    override fun fromAvro(value: Int): MyIntType = MyIntType(value)
    override fun toAvro(value: MyIntType): Int = value.value
  }

  class TypeSerializer : IntLogicalTypeSerializer<TestIntLogicalType, MyIntType>(TypeConversion())

  class TypeSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(MyIntType::class, TypeSerializer())
    }
  }
}
