package io.toolisticon.kotlin.avro.serialization.serializer._fixtures

import io.toolisticon.kotlin.avro.logical.LongLogicalType
import io.toolisticon.kotlin.avro.logical.LongLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.LongLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.LongLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.serializer.LongLogicalTypeSerializerTest.LongType
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object TestLongLogicalType : LongLogicalType("long-type".toLogicalTypeName()) {

  @Suppress("unused")
  class LogicalTypeFactory : LongLogicalTypeFactory<TestLongLogicalType>(logicalType = TestLongLogicalType)

  class TypeConversion : LongLogicalTypeConversion<TestLongLogicalType, LongType>(TestLongLogicalType, LongType::class) {
    override fun fromAvro(value: Long): LongType = LongType(value)
    override fun toAvro(value: LongType): Long = value.value
  }

  class TypeSerializer : LongLogicalTypeSerializer<TestLongLogicalType, LongType>(TypeConversion())

  class TypeSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(LongType::class, TypeSerializer())
    }
  }
}
