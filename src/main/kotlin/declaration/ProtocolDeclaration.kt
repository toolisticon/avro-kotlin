package io.toolisticon.avro.kotlin.declaration

import io.toolisticon.avro.kotlin.model.AvroProtocol
import io.toolisticon.avro.kotlin.model.AvroSource
import io.toolisticon.avro.kotlin.model.AvroTypesMap
import io.toolisticon.avro.kotlin.value.Documentation
import io.toolisticon.avro.kotlin.value.Documentation.Companion.shortenedIfPresent
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.Protocol

/**
 * The result of parsing an `*.avpr` protocol declaration.
 */
data class ProtocolDeclaration(
  override val originalJson: JsonString,

  override val namespace: Namespace,
  override val name: Name,
  val documentation: Documentation?,

  val protocol: Protocol,
  override val avroTypes: AvroTypesMap = AvroTypesMap(AvroProtocol(protocol))
) : AvroDeclaration {
  override val source: AvroSource
    get() = TODO("Not yet implemented")


//  val catalog: AvroParser.model.AvroSchemaCatalog = (listOf<Schema>() + protocol.types + protocol.messages.values.flatMap {
//    listOf(
//      it.request,
//      it.response,
//      it.errors
//    )
//  }).fold(AvroSchemaCatalogBak(emptyList())) { acc, cur ->
//    AvroSchemaCatalogBak(buildList {
//      addAll(acc)
//      addAll(AvroSchemaCatalogBak(AvroSchema(cur)))
//    })
//  }


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
