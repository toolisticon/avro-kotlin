package io.toolisticon.kotlin.avro.model.wrapper

import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.Name
import org.apache.avro.Schema
import java.util.function.Supplier

/**
 * Provide access to a wrapped [Schema].
 */
interface SchemaSupplier : Supplier<Schema> {

  /**
   * The hashCode identifies a [Schema].
   */
  val hashCode: AvroHashCode

  /**
   * The JSON representation of this avro declaration.
   */
  val json: JsonString

  /**
   * The name. In case of named types: simple name, else: Type name.
   */
  val name: Name
}
