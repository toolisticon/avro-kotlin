package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
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
