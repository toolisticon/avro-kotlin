package io.toolisticon.avro.kotlin.declaration

import io.toolisticon.avro.kotlin.model.AvroSource
import io.toolisticon.avro.kotlin.model.AvroTypesMap
import io.toolisticon.avro.kotlin.model.RecordField
import io.toolisticon.avro.kotlin.model.RecordType
import io.toolisticon.avro.kotlin.value.Documentation
import io.toolisticon.avro.kotlin.value.Documentation.Companion.shortenedIfPresent
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.Schema

/**
 * The result of parsing an `*.avsc` file.
 */
data class SchemaDeclaration(
  override val originalJson: JsonString,
  override val source: AvroSource,

  val schema: Schema,

  override val namespace: Namespace,
  override val name: Name,
  val documentation: Documentation?,

  val recordType: RecordType,
  override val avroTypes: AvroTypesMap,
  val fields: List<RecordField> = recordType.fields,
) : AvroDeclaration {

  override fun toString() = "SchemaDeclaration(" +
    "namespace='$namespace'" +
    ", name='$name'" +
    documentation.shortenedIfPresent() +
    ", avroTypes=$avroTypes" +
    ", fields=$fields" +
    ")"

}
