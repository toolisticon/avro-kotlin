package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import java.util.function.Supplier

typealias SchemaSupplier = Supplier<Schema>

/**
 * All avro types implement this interface. These are:
 *
 * Named:
 *
 *   * RECORD
 *   * ENUM
 *   * FIXED
 *
 * Containers:
 *   * ARRAY
 *   * MAP
 *   * UNION
 *
 *  Primitive:
 *    * BOOLEAN
 *    * BYTES
 *    * DOUBLE
 *    * FLOAT
 *    * INT
 *    * LONG
 *    * NULL
 *    * STRING
 */
sealed interface AvroType : SchemaSupplier {
  /**
   * The name. In case of named types: simple name, else: Type name.
   */
  val name: Name

  /**
   * The schema hashCode. This includes logicaltypes and additional properties and uniquely identifies a [Schema].
   */
  val hashCode: AvroHashCode

  /**
   * The fingerprint to identify a schema in a [org.apache.avro.message.SchemaStore]. This ignores all logicaltypes and additional properties and is not sufficient to uniquely identify a [Schema].
   */
  val fingerprint: AvroFingerprint

  /**
   * The wrapped avro [AvroSchema] of this type, including all meta-data.
   */
  val schema: AvroSchema
}

/**
 * Marks the primitive types: BOOLEAN, BYTES, DOUBLE, FLOAT, INT, LONG, NULL, STRING.
 */
sealed interface AvroPrimitiveType : AvroType {
  val type: Schema.Type
}

/**
 * A named type has a namespace and possibly documentation.
 *
 * Types: FIXED, RECORD, ENUM
 *
 * Note: FIXED is currently not supported
 */
sealed interface AvroNamedType : AvroType, WithDocumentation {
  val namespace: Namespace?
}

/**
 * A container type is an anonymous wrapper around other [AvroType](s).
 *
 * These are:
 *
 * * ARRAY - a list of wrapped subTypes.
 * * MAP - a map with string keys and values of wrapped subType.
 * * UNION - array of subTypes with "oneOf" semantic. Currently only unions of null and one other type are supported, effectively resulting in an optional type.
 */
sealed interface AvroContainerType : AvroType


/**
 * Marks [AvroType]s that may have a logical types.
 * Default supported logical types are:
 *
 * * date - on INT
 * * decimal - on BYTES and FIXED
 * * duration - on FIXED(12)
 * * local-timestamp-millis - on LONG
 * * local-timestamp-micros - on LONG
 * * time-millis - on LONG
 * * timestamp-micros - on LONG
 * * timestamp-millis - on LONG
 * * uuid - on STRING
 *
 * Types with possible logicalType:
 *
 * * BYTES
 * * FIXED
 * * INT
 * * LONG
 * * STRING
 */
sealed interface WithLogicalType {
  val logicalType: LogicalType?

  val logicalTypeName: LogicalTypeName?

  fun hasLogicalType() = logicalType != null
}

sealed interface WithDocumentation {
  val documentation: Documentation?
}

