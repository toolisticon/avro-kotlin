package io.toolisticon.kotlin.avro.generator.logical

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.Avro4kSerializerKClass
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.WithLogicalType
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi
import io.toolisticon.kotlin.generation.spi.processor.KotlinConstructorPropertySpecProcessor
import io.toolisticon.kotlin.generation.support.ContextualAnnotation
import kotlin.reflect.KClass


/**
 * A logical type definition is a special kind of [Avro4kGeneratorProcessor] as it defines the attributes required to identity and intepret a logical type but
 * also provides the necessary [DataClassParameterSpecProcessor] functionality to apply the type on properties.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class AvroKotlinLogicalTypeDefinition(
  val logicalTypeName: LogicalTypeName,
  val convertedType: KClass<*>,
  val serializerType: Avro4kSerializerKClass,
  val allowedTypes: Set<SchemaType>,
) : KotlinConstructorPropertySpecProcessor<SchemaDeclarationContext, RecordField>(
  contextType = SchemaDeclarationContext::class,
  inputType = RecordField::class,
  order = KotlinCodeGenerationSpi.DEFAULT_ORDER
) {

  override fun test(ctx: SchemaDeclarationContext, input: Any): Boolean = when (input) {
    is RecordField -> {
      val avroType = ctx[input.hashCode].avroType
      avroType is WithLogicalType && avroType.hasLogicalType() && logicalTypeName == avroType.logicalTypeName && allowedTypes.contains(avroType.schema.type)
    }

    else -> false
  }

  override fun invoke(
    context: SchemaDeclarationContext, input: RecordField, builder: KotlinConstructorPropertySpecBuilder
  ): KotlinConstructorPropertySpecBuilder {
    if (test(context, input)) {
      builder.addAnnotation(ContextualAnnotation)
    }
    return builder
  }

  override fun toString() =
    "Avro4kLogicalTypeDefinition(name=$name, convertedType=$convertedType, serializerType=$serializerType, allowedTypes=$allowedTypes)"

}
