package io.toolisticon.avro.kotlin.declaration

import io.toolisticon.avro.kotlin.model.AvroSource
import io.toolisticon.avro.kotlin.model.AvroTypesMap
import io.toolisticon.avro.kotlin.model.WithDocumentation
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace

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
