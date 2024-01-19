package io.toolisticon.avro.kotlin.declaration

import io.toolisticon.avro.kotlin.model.AvroSource
import io.toolisticon.avro.kotlin.model.AvroTypesMap
import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.Documentation
import io.toolisticon.avro.kotlin.value.Documentation.Companion.shortenedIfPresent
import io.toolisticon.avro.kotlin.value.JsonString

/**
 * The result of parsing an `*.avpr` protocol declaration.
 */
class ProtocolDeclaration(
  val protocol: AvroProtocol,
  override val source: AvroSource
) : AvroDeclaration {

  override val originalJson: JsonString = source.json

  override val documentation: Documentation? = protocol.documentation

  override val avroTypes: AvroTypesMap = protocol.types
  override val canonicalName: CanonicalName = protocol.canonicalName

  override fun toString() = "ProtocolDeclaration(" +
    "namespace='$namespace'" +
    ", name='$name'" +
    documentation.shortenedIfPresent() +
    ", avroTypes=$avroTypes" +
    //", fields=$fields" +
    ")"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProtocolDeclaration) return false
    if (protocol != other.protocol) return false
    return true
  }

  override fun hashCode(): Int = protocol.hashCode()
}
