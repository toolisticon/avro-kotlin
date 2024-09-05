package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetType
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.model.AvroTypesMap
import io.toolisticon.kotlin.avro.model.wrapper.AvroSource
import io.toolisticon.kotlin.avro.value.*
import io.toolisticon.kotlin.generation.FileName

/**
 * The generator context, holds all data required for strategies and processors to do their work.
 * Also knows the registry of existing strategies and processors.
 */
sealed interface AvroDeclarationContext {

  /**
   * Everything we generate will reside under this root package.
   */
  val rootPackageNamespace: Namespace get() = canonicalName.namespace

  /**
   * The root class name of the [com.squareup.kotlinpoet.FileSpec] we are building.
   */
  val rootClassName: FileName? get() = null

  /**
   * What was the source of this declaration?
   */
  val source: AvroSource

  val canonicalName: CanonicalName

  /**
   * For reference purposes, we keep the unmodified json as we read it from source.
   */
  val originalJson: JsonString get() = source.json

  /**
   * On Top level, we need a namespace.
   */
  val namespace: Namespace get() = canonicalName.namespace

  /**
   * The name of the root type.
   */
  val name: Name get() = canonicalName.name

  val avroTypes: AvroTypesMap

  /**
   * Indicator if we are working on the root [com.squareup.kotlinpoet.TypeSpec] of our generated file.
   * This is useful if we for example have nested data classes and want to apply certain annotations, docs, ... only on top level.
   *
   * TODO: do we need this on top level? it might be relevant for schema, but not for protocol
   */
  val isRoot: Boolean

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

  /**
   * Get an [AvroPoetType] from [avroPoetTypes]. Nullsafe, fails if type can not be found (which should never be the case).
   */
  operator fun get(hashCode: AvroHashCode): AvroPoetType = requireNotNull(avroPoetTypes[hashCode]) { "type not found for hashCode=$hashCode" }

  fun avroType(hashCode: AvroHashCode) = get(hashCode).avroType
  fun typeName(hashCode: AvroHashCode) = get(hashCode).typeName
  fun className(hashCode: AvroHashCode) = typeName(hashCode) as ClassName

  val generatedTypes : MutableMap<AvroFingerprint, TypeName>
}
