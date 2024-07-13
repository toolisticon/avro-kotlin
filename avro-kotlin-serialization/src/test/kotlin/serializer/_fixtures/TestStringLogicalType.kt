package io.toolisticon.kotlin.avro.serialization.serializer._fixtures

import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.logical.StringLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.StringLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.StringLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule
import serializer.StringLogicalTypeSerializerTest.StringType

object TestStringLogicalType : StringLogicalType("string-type".toLogicalTypeName()) {

  class LogicalTypeFactory : StringLogicalTypeFactory<TestStringLogicalType>(logicalType = TestStringLogicalType)

  class TypeConversion : StringLogicalTypeConversion<TestStringLogicalType, StringType>(TestStringLogicalType, StringType::class) {
    override fun fromAvro(value: String): StringType = StringType(value)
    override fun toAvro(value: StringType): String = value.value
  }

  class TypeSerializer : StringLogicalTypeSerializer<TestStringLogicalType, StringType>(TypeConversion())

  class TypeSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(StringType::class, TypeSerializer())
    }
  }
}
