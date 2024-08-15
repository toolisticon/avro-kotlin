package io.toolisticon.kotlin.avro.generator.logical

import io.toolisticon.kotlin.avro.generator.Avro4kSerializerKClass
import io.toolisticon.kotlin.avro.generator.context.AvroDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.WithLogicalType
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi
import io.toolisticon.kotlin.generation.spi.processor.ConstructorPropertySpecProcessor
import kotlin.reflect.KClass


/**
 * A logical type definition is a special kind of [Avro4kGeneratorProcessor] as it defines the attributes required to identity and intepret a logical type but
 * also provides the necessary [DataClassParameterSpecProcessor] functionality to apply the type on properties.
 */
abstract class AvroKotlinLogicalTypeDefinition(
  val logicalTypeName: LogicalTypeName,
  val convertedType: KClass<*>,
  val serializerType: Avro4kSerializerKClass,
  val allowedTypes: Set<SchemaType>,

  val processDataClassParameterSpecPredicate: (AvroDeclarationContext<*>, RecordField) -> Boolean = { ctx, field ->
    val avroType = ctx[field.hashCode].avroType
    avroType is WithLogicalType
      && avroType.hasLogicalType()
      && logicalTypeName == avroType.logicalTypeName
      && allowedTypes.contains(avroType.schema.type)
  },


  ) : ConstructorPropertySpecProcessor<AvroDeclarationContext<*>, RecordField>(
  contextType = AvroDeclarationContext::class,
  inputType = RecordField::class,
  order = KotlinCodeGenerationSpi.DEFAULT_ORDER
) {

  override fun invoke(
    context: AvroDeclarationContext<*>,
    input: RecordField?,
    builder: KotlinConstructorPropertySpecBuilder
  ): KotlinConstructorPropertySpecBuilder {
    TODO("Not yet implemented")
  }

  override fun toString() = "Avro4kLogicalTypeDefinition(" +
    "name=$name" +
    ", convertedType=$convertedType" +
    ", serializerType=$serializerType" +
    ", allowedTypes=$allowedTypes" +
    ", processDataClassParameterSpecPredicate=$processDataClassParameterSpecPredicate" +
    ")"

}
