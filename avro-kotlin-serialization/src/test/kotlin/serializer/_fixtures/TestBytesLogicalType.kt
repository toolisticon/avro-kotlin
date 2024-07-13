package io.toolisticon.kotlin.avro.serialization.serializer._fixtures

import io.toolisticon.kotlin.avro.logical.BooleanLogicalType
import io.toolisticon.kotlin.avro.logical.BooleanLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.BytesLogicalType
import io.toolisticon.kotlin.avro.logical.BytesLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.BooleanLogicalTypeConversion
import io.toolisticon.kotlin.avro.logical.conversion.BytesLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.BooleanLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.serializer.BooleanLogicalTypeSerializerTest.MyBooleanType
import io.toolisticon.kotlin.avro.serialization.serializer.BytesLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.serializer.BytesLogicalTypeSerializerTest
import io.toolisticon.kotlin.avro.serialization.serializer.BytesLogicalTypeSerializerTest.MyBytesType
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object TestBytesLogicalType : BytesLogicalType("bytes-type".toLogicalTypeName()) {

  @Suppress("unused")
  class LogicalTypeFactory : BytesLogicalTypeFactory<TestBytesLogicalType>(logicalType = TestBytesLogicalType)

  class TypeConversion : BytesLogicalTypeConversion<TestBytesLogicalType, MyBytesType>(TestBytesLogicalType, MyBytesType::class) {
    override fun fromAvro(value: ByteArray): MyBytesType = MyBytesType(value)
    override fun toAvro(value: MyBytesType): ByteArray = value.value
  }

  class TypeSerializer : BytesLogicalTypeSerializer<TestBytesLogicalType, MyBytesType>(TypeConversion())

  class TypeSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(MyBytesType::class, TypeSerializer())
    }
  }
}
