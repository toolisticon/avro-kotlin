@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.logical.LogicalTypeMap
import io.toolisticon.kotlin.avro.generator.strategy.internal.KotlinConstructorPropertyStrategy
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.spi.defaultClassLoader
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry
import kotlin.reflect.KClass

/**
 * An implementation of [KotlinCodeGenerationSpiRegistry] that supports avro generation specific helpers.
 */
class AvroCodeGenerationSpiRegistry(registry: KotlinCodeGenerationSpiRegistry) : KotlinCodeGenerationSpiRegistry by registry {
  companion object {

    fun load(classLoader: ClassLoader = defaultClassLoader()): AvroCodeGenerationSpiRegistry {
      val registry = KotlinCodeGeneration.spi.registry(contextTypeUpperBound = AvroDeclarationContext::class, classLoader = classLoader)
      return AvroCodeGenerationSpiRegistry(registry)
    }
  }

  override val contextTypeUpperBound: KClass<*> = AvroDeclarationContext::class

  val logicalTypes: LogicalTypeMap = LogicalTypeMap(this)

  internal val constructorPropertyStrategy = KotlinConstructorPropertyStrategy()

}
