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
import io.toolisticon.kotlin.generation.spi.strategy.KotlinFileSpecStrategy
import kotlin.reflect.KClass

/**
 * Base strategy for generating TypeSpecs (DataClass, EnumClass, ...)
 * (TODO: add more, see issue #114) )
 * from a given [AvroNamedType] input.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
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
@OptIn(ExperimentalKotlinPoetApi::class)
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
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class AvroEnumTypeSpecStrategy : AvroNamedTypeSpecStrategy<EnumType, KotlinEnumClassSpec>(
  inputType = EnumType::class, specType = KotlinEnumClassSpec::class
) {
  abstract override fun invoke(context: SchemaDeclarationContext, input: EnumType): KotlinEnumClassSpec
  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean =
    input != null && input is EnumType && !ctx.isRoot
}

/**
 * Use this base class to implement a strategy that takes a [ProtocolDeclaration] and uses the
 * data provided to create a [KotlinFileSpec].
 *
 * If more than one [AvroFileSpecFromProtocolDeclarationStrategy] is registered, multiple files will be created.
 * If they relay on each other, make sure that the ordering is correct. Sequential calls of multiple strategies may share their
 * generated state using a mutable context (strategy `A` creates base types, strategy `B` reuses these types to provide messages interfaces).
 */
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class AvroFileSpecFromProtocolDeclarationStrategy : KotlinFileSpecStrategy<ProtocolDeclarationContext, ProtocolDeclaration>(
  contextType = ProtocolDeclarationContext::class, inputType = ProtocolDeclaration::class
) {
  override fun test(context: ProtocolDeclarationContext, input: Any?): Boolean = super.test(context, input)

  abstract override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec
}

/**
 * Use this base class to implement a strategy that takes a [SchemaDeclaration] and uses the
 * data provided to create a [KotlinFileSpec].
 *
 * If more than one [AvroFileSpecFromSchemaDeclarationStrategy] is registered, multiple files will be created.
 * If they relay on each other, make sure that the ordering is correct. Sequential calls of multiple strategies may share their
 * generated state using a mutable context (strategy `A` creates base types, strategy `B` reuses these types to provide messages interfaces).
 */
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class AvroFileSpecFromSchemaDeclarationStrategy : KotlinCodeGenerationStrategyBase<SchemaDeclarationContext, SchemaDeclaration, KotlinFileSpec>(
  contextType = SchemaDeclarationContext::class, inputType = SchemaDeclaration::class, specType = KotlinFileSpec::class
) {

  abstract override fun invoke(context: SchemaDeclarationContext, input: SchemaDeclaration): KotlinFileSpec

  override fun test(context: SchemaDeclarationContext, input: Any?): Boolean = super.test(context, input)
}
