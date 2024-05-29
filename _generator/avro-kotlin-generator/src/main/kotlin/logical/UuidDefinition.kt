package io.toolisticon.kotlin.avro.generator.logical

import com.github.avrokotlin.avro4k.serializer.UUIDSerializer
import io.toolisticon.kotlin.avro.generator.api.processor.AvroKotlinLogicalTypeDefinition
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import io.toolisticon.kotlin.avro.model.SchemaType
import java.util.*

/**
 * The uuid logical type represents a random generated universally unique identifier (UUID).
 *
 * A uuid logical type annotates an Avro string. The string has to conform with RFC-4122
 *
 * @see [LogicalType#UUID](https://avro.apache.org/docs/current/specification/#uuid)
 */
// FIXME @AutoService(Avro4kLogicalTypeDefinition::class)
class UuidDefinition : AvroKotlinLogicalTypeDefinition(
  name = BuiltInLogicalType.UUID.logicalTypeName,
  convertedType = UUID::class,
  serializerType = UUIDSerializer::class,
  allowedTypes = setOf(SchemaType.STRING)
)
