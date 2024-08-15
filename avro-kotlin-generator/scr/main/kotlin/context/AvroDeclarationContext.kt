package io.toolisticon.kotlin.avro.generator.context

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetType
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.generation.FileName
import io.toolisticon.kotlin.generation.spi.AbstractKotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry

/**
 * The generator context, holds all data required for strategies and processors to do their work.
 * Also knows the registry of existing strategies and processors.
 */
sealed interface AvroDeclarationContext<T : AvroDeclaration>: KotlinCodeGenerationContext<AvroDeclarationContext<*>> {

  /**
   * The root class name of the [com.squareup.kotlinpoet.FileSpec] we are building.
   */
  val rootClassName: FileName

  /**
   * Indicator if we are working on the root [com.squareup.kotlinpoet.TypeSpec] of our generated file.
   * This is useful if we for example have nested data classes and want to apply certain annotations, docs, ... only on top level.
   *
   * TODO: do we need this on top level? it might be relevant for schema, but not for protocol
   */
  val isRoot: Boolean

  /**
   * The [AvroDeclaration] we derived from the parsed avro json, either [SchemaDeclaration] or [ProtocolDeclaration].
   */
  val declaration: T

  /**
   * The configured properties for the generator.
   */
  val properties: AvroKotlinGeneratorProperties

  /**
   * A map of [SchemaKey] to [io.toolisticon.avro.kotlin.types.AvroType] and resolved [com.squareup.kotlinpoet.TypeName].
   *
   * We can use this information to set type/class names of fields, subclasses, ... directly since this information is already prepared.
   */
  val avroPoetTypes: AvroPoetTypes

//  /**
//   * All defined processors for x-use in other generators.
//   *
//   * example: We create an additional data class in the FileSpec and need to run
//   * the same parameter modifiers as in the default root data class.
//   */
//  val processors: AvroKotlinGeneratorProcessors
//
//  val strategies: AvroKotlinGeneratorStrategies

  /**
   * Get an [AvroPoetType] from [avroPoetTypes]. Nullsafe, fails if type can not be found (which should never be the case).
   */
  operator fun get(hashCode: AvroHashCode): AvroPoetType = requireNotNull(avroPoetTypes[hashCode]) { "type not found for hashCode=$hashCode" }

  fun avroType(hashCode: AvroHashCode) = get(hashCode).avroType
  fun typeName(hashCode: AvroHashCode) = get(hashCode).typeName
  fun className(hashCode: AvroHashCode) = typeName(hashCode) as ClassName
}
