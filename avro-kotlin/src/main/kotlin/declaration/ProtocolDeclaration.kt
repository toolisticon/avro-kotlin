package io.toolisticon.kotlin.avro.declaration

import io.toolisticon.kotlin.avro.model.AvroTypesMap
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.model.wrapper.AvroSource
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.Documentation
import io.toolisticon.kotlin.avro.value.Documentation.Companion.shortenedIfPresent
import io.toolisticon.kotlin.avro.value.JsonString

/**
 * The result of parsing an `*.avpr` protocol declaration.
 */
class ProtocolDeclaration(
  val protocol: AvroProtocol,
  override val source: AvroSource
) : AvroDeclaration {

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
