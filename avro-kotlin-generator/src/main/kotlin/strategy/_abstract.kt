@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.spi.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.spec.KotlinConstructorPropertySpecSupplier
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spec.KotlinGeneratorTypeSpec
import io.toolisticon.kotlin.generation.spi.strategy.KotlinCodeGenerationStrategyBase
import kotlin.reflect.KClass

/**
 * Base strategy for generating TypeSpecs (DataClass, EnumClass, ...)
 * (TODO: add more, see issue #114) )
 * from a given [AvroNamedType] input.
 */
abstract class AvroNamedTypeSpecStrategy<INPUT : AvroNamedType, SPEC : KotlinGeneratorTypeSpec<SPEC>>(
  inputType: KClass<INPUT>, specType: KClass<SPEC>,
) : KotlinCodeGenerationStrategyBase<SchemaDeclarationContext, INPUT, SPEC>(
  contextType = SchemaDeclarationContext::class, inputType = inputType, specType = specType
) {
  abstract override fun invoke(context: SchemaDeclarationContext, input: INPUT): SPEC
  abstract override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean
}

/**
 * Base strategy to create a [KotlinDataClassSpec] from a [RecordType] input.
 */
abstract class AvroRecordTypeSpecStrategy : AvroNamedTypeSpecStrategy<RecordType, KotlinDataClassSpec>(
  inputType = RecordType::class, specType = KotlinDataClassSpec::class
) {
  abstract override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec
  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean = input != null && input is RecordType

  protected fun parameterSpecs(
    context: SchemaDeclarationContext,
    input: RecordType
  ): List<KotlinConstructorPropertySpecSupplier> {
    return input.fields.map { context.registry.constructorPropertyStrategy(context, it) }
  }
}

/**
 * Base strategy to create a [KotlinEnumClassSpec] from a [EnumType] input.
 */
abstract class AvroEnumTypeSpecStrategy : AvroNamedTypeSpecStrategy<EnumType, KotlinEnumClassSpec>(
  inputType = EnumType::class, specType = KotlinEnumClassSpec::class
) {
  abstract override fun invoke(context: SchemaDeclarationContext, input: EnumType): KotlinEnumClassSpec
  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean =
    input != null && input is EnumType && !ctx.isRoot
}

abstract class AvroFileSpecFromProtocolDeclarationStrategy : KotlinCodeGenerationStrategyBase<ProtocolDeclarationContext, ProtocolDeclaration, KotlinFileSpec>(
  contextType = ProtocolDeclarationContext::class, inputType = ProtocolDeclaration::class, specType = KotlinFileSpec::class
) {
  override fun test(context: ProtocolDeclarationContext, input: Any?): Boolean = super.test(context, input)

  abstract override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec
}


abstract class AvroFileSpecFromSchemaDeclarationStrategy : KotlinCodeGenerationStrategyBase<SchemaDeclarationContext, SchemaDeclaration, KotlinFileSpec>(
  contextType = SchemaDeclarationContext::class, inputType = SchemaDeclaration::class, specType = KotlinFileSpec::class
) {
  override fun test(context: SchemaDeclarationContext, input: Any?): Boolean = super.test(context, input)

  abstract override fun invoke(context: SchemaDeclarationContext, input: SchemaDeclaration): KotlinFileSpec
}
