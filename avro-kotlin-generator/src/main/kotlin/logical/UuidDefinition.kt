package io.toolisticon.kotlin.avro.generator.logical

import com.github.avrokotlin.avro4k.serializer.UUIDSerializer
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType

class UuidDefinition : AvroKotlinLogicalTypeDefinition(
  logicalTypeName = BuiltInLogicalType.UUID.logicalTypeName,
  convertedType = BuiltInLogicalType.UUID.convertedType.kotlin,
  serializerType = UUIDSerializer::class,
  allowedTypes = BuiltInLogicalType.UUID.allowedTypes
) {
}
