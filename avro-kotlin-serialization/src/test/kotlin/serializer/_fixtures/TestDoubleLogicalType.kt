package io.toolisticon.kotlin.avro.serialization.serializer._fixtures

import io.toolisticon.kotlin.avro.logical.*
import io.toolisticon.kotlin.avro.logical.conversion.DoubleLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.*
import io.toolisticon.kotlin.avro.serialization.serializer.DoubleLogicalTypeSerializerTest.MyDoubleType
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object TestDoubleLogicalType : DoubleLogicalType("double-type".toLogicalTypeName()) {

  @Suppress("unused")
  class LogicalTypeFactory : DoubleLogicalTypeFactory<TestDoubleLogicalType>(logicalType = TestDoubleLogicalType)

  class TypeConversion : DoubleLogicalTypeConversion<TestDoubleLogicalType, MyDoubleType>(TestDoubleLogicalType, MyDoubleType::class) {
    override fun fromAvro(value: Double): MyDoubleType = MyDoubleType(value)
    override fun toAvro(value: MyDoubleType): Double = value.value
  }

  class TypeSerializer : DoubleLogicalTypeSerializer<TestDoubleLogicalType, MyDoubleType>(TypeConversion())

  class TypeSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(MyDoubleType::class, TypeSerializer())
    }
  }
}
