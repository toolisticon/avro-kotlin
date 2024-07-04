package io.toolisticon.kotlin.avro.generator.api.processor

import io.toolisticon.kotlin.avro.generator.api.Avro4kSerializerKClass
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.SerializableWithAnnotation
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpiList
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.WithLogicalType
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import kotlin.reflect.KClass

/**
 * A logical type definition is a special kind of [Avro4kGeneratorProcessor] as it defines the attributes required to identity and intepret a logical type but
 * also provides the necessary [DataClassParameterSpecProcessor] functionality to apply the type on properties.
 */
abstract class AvroKotlinLogicalTypeDefinition(
  val name: LogicalTypeName,
  val convertedType: KClass<*>,
  val serializerType: Avro4kSerializerKClass,
  val allowedTypes: Set<SchemaType>,

  override val processDataClassParameterSpecPredicate: (AvroDeclarationContext, RecordField) -> Boolean = { ctx, field ->
    val avroType = ctx[field.hashCode].avroType
    avroType is WithLogicalType
      && avroType.hasLogicalType()
      && name == avroType.logicalTypeName
      && allowedTypes.contains(avroType.schema.type)
  },

  override val order: Int = AvroKotlinGeneratorSpi.DEFAULT_ORDER

) : DataClassParameterSpecProcessor {
  companion object {

    operator fun AvroKotlinLogicalTypeDefinition.invoke(
      ctx: AvroDeclarationContext,
      field: RecordField,
      builder: KotlinConstructorPropertySpecBuilder
    ) = if (processDataClassParameterSpecPredicate(ctx, field)) {
      processDataClassParameterSpec(ctx, field, builder)
    } else {
    }
  }

  override fun processDataClassParameterSpec(ctx: AvroDeclarationContext, field: RecordField, builder: KotlinConstructorPropertySpecBuilder) {
    builder.addAnnotation(SerializableWithAnnotation(serializerType))
  }

  override fun toString() = "Avro4kLogicalTypeDefinition(" +
    "name=$name" +
    ", convertedType=$convertedType" +
    ", serializerType=$serializerType" +
    ", allowedTypes=$allowedTypes" +
    ", processDataClassParameterSpecPredicate=$processDataClassParameterSpecPredicate" +
    ")"

}

@JvmInline
value class LogicalTypeMap(
  private val map: Map<LogicalTypeName, AvroKotlinLogicalTypeDefinition>
) : Map<LogicalTypeName, AvroKotlinLogicalTypeDefinition> by map {
  constructor(spis: AvroKotlinGeneratorSpiList) : this(spis.filterIsInstance(AvroKotlinLogicalTypeDefinition::class).associateBy { it.name })
}
