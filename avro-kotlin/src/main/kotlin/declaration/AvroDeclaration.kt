package io.toolisticon.kotlin.avro.declaration

import io.toolisticon.kotlin.avro.model.AvroTypesMap
import io.toolisticon.kotlin.avro.model.WithDocumentation
import io.toolisticon.kotlin.avro.model.wrapper.AvroSource
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace

/**
 * The result of an AvroParser parse/process.
 *
 * Contains the original parsed json structure (Schema or Protocol)
 * and a deep analysis/breakdown of all contained sub-schemas.
 */
sealed interface AvroDeclaration : WithDocumentation {
  val canonicalName: CanonicalName

  /**
   * For reference purposes, we keep the unmodified json as we read it from source.
   */
  val originalJson: JsonString

  /**
   * What was the source of this declaration?
   */
  val source: AvroSource

  /**
   * On Top level, we need a namespace.
   */
  val namespace: Namespace get() = canonicalName.namespace

  /**
   * The name of the root type.
   */
  val name: Name get() = canonicalName.name

  val avroTypes: AvroTypesMap
}
