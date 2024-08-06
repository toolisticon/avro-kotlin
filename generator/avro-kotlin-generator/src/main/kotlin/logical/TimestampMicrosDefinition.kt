package io.toolisticon.kotlin.avro.generator.logical

import com.github.avrokotlin.avro4k.serializer.InstantToMicroSerializer
import io.toolisticon.kotlin.avro.generator.api.processor.AvroKotlinLogicalTypeDefinition
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import io.toolisticon.kotlin.avro.model.SchemaType
import java.time.Instant

// FIXME @AutoService(Avro4kLogicalTypeDefinition::class)
class TimestampMicrosDefinition : AvroKotlinLogicalTypeDefinition(
  name = BuiltInLogicalType.TIMESTAMP_MICROS.logicalTypeName,
  convertedType = Instant::class,
  serializerType = InstantToMicroSerializer::class,
  allowedTypes = setOf(SchemaType.LONG)
)
