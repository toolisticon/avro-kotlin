package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi
import io.toolisticon.kotlin.generation.spi.strategy.AbstractKotlinCodeGenerationStrategy
import io.toolisticon.kotlin.generation.spi.strategy.DataClassSpecStrategy
import kotlin.reflect.KClass

@OptIn(ExperimentalKotlinPoetApi::class)
abstract class AbstractDataClassFromRecordTypeStrategy :
  DataClassSpecStrategy<SchemaDeclarationContext, RecordType>(
    contextType = SchemaDeclarationContext::class, inputType = RecordType::class
  )



