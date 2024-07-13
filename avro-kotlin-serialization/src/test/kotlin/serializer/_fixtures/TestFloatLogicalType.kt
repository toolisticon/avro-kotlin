package io.toolisticon.kotlin.avro.serialization.serializer._fixtures

import io.toolisticon.kotlin.avro.logical.FloatLogicalType
import io.toolisticon.kotlin.avro.logical.FloatLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.IntLogicalType
import io.toolisticon.kotlin.avro.logical.IntLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.FloatLogicalTypeConversion
import io.toolisticon.kotlin.avro.logical.conversion.IntLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.FloatLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.serializer.FloatLogicalTypeSerializerTest
import io.toolisticon.kotlin.avro.serialization.serializer.FloatLogicalTypeSerializerTest.*
import io.toolisticon.kotlin.avro.serialization.serializer.IntLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.serializer.IntLogicalTypeSerializerTest.MyIntType
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object TestFloatLogicalType : FloatLogicalType("float-type".toLogicalTypeName()) {

  @Suppress("unused")
  class LogicalTypeFactory : FloatLogicalTypeFactory<TestFloatLogicalType>(logicalType = TestFloatLogicalType)

  class TypeConversion : FloatLogicalTypeConversion<TestFloatLogicalType, MyFloatType>(TestFloatLogicalType, MyFloatType::class) {
    override fun fromAvro(value: Float): MyFloatType = MyFloatType(value)
    override fun toAvro(value: MyFloatType): Float = value.value
  }

  class TypeSerializer : FloatLogicalTypeSerializer<TestFloatLogicalType, MyFloatType>(TypeConversion())

  class TypeSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(MyFloatType::class, TypeSerializer())
    }
  }
}
