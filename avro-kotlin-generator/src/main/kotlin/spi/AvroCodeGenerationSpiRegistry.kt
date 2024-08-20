package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.context.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.logical.LogicalTypeMap
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.spi.defaultClassLoader
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry
import kotlin.reflect.KClass

@OptIn(ExperimentalKotlinPoetApi::class)
class AvroCodeGenerationSpiRegistry(registry: KotlinCodeGenerationSpiRegistry) : KotlinCodeGenerationSpiRegistry by registry {
  companion object {

    fun load(classLoader: ClassLoader = defaultClassLoader()): AvroCodeGenerationSpiRegistry {
      val registry = KotlinCodeGeneration.spi.registry(contextTypeUpperBound = AvroDeclarationContext::class, classLoader = classLoader)
      return AvroCodeGenerationSpiRegistry(registry)
    }
  }

  override val contextTypeUpperBound: KClass<*> = AvroDeclarationContext::class

  val logicalTypes: LogicalTypeMap = LogicalTypeMap(this)

//  val constructorPropertiesProcessor: ConstructorPropertySpecProcessorList<SchemaDeclarationContext, RecordField> =
//    ConstructorPropertySpecProcessorList.of(registry)
}
