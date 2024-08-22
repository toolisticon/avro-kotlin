@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.processor.AbstractPropertyFromRecordFieldProcessor
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinConstructorPropertySpecSupplier
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinGeneratorTypeSpec
import io.toolisticon.kotlin.generation.spi.processor.executeAll
import io.toolisticon.kotlin.generation.spi.strategy.AbstractKotlinCodeGenerationStrategy
import io.toolisticon.kotlin.generation.spi.strategy.DataClassSpecStrategy
import kotlin.reflect.KClass

/**
 * Base strategy for generating TypeSpecs (DataClass, EnumClass, ...)
 * (TODO: add more) )
 * from a given [AvroNamedType] input.
 */
abstract class AvroNamedTypeSpecStrategy<INPUT : AvroNamedType, SPEC : KotlinGeneratorTypeSpec<SPEC>>(
    inputType: KClass<INPUT>, specType: KClass<SPEC>,
) : AbstractKotlinCodeGenerationStrategy<SchemaDeclarationContext, INPUT, SPEC>(
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
        return input.fields.map { field ->
            val typeName = context[field.hashCode].suffixedTypeName
            val propertyBuilder = KotlinConstructorPropertySpecBuilder.builder(name = field.name.value, typeName)
            // TODO: use generic processor?
            context.processors(AbstractPropertyFromRecordFieldProcessor::class)
                .executeAll(context, field, propertyBuilder)
        }
    }
}

/**
 * Base strategy to create a [KotlinEnumClassSpec] from a [EnumType] input.
 */
abstract class AvroEnumTypeSpecStrategy : AvroNamedTypeSpecStrategy<EnumType, KotlinEnumClassSpec>(
    inputType = EnumType::class, specType = KotlinEnumClassSpec::class
) {
    abstract override fun invoke(context: SchemaDeclarationContext, input: EnumType): KotlinEnumClassSpec
    override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean = input != null && input is EnumType && !ctx.isRoot
}

@Deprecated("replaced by AvroRecordTypeSpecStrategy")
abstract class AbstractDataClassFromRecordTypeStrategy : DataClassSpecStrategy<SchemaDeclarationContext, RecordType>(
    contextType = SchemaDeclarationContext::class, inputType = RecordType::class
)



