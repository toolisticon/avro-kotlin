package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isEmptyType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isError
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isOptionalType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass

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
sealed interface AvroType : SchemaSupplier, WithObjectProperties {

  companion object {


    /**
     * Factory method to create [AvroType] from [AvroSchema].
     */
    inline fun <reified T : AvroType> avroType(schema: AvroSchema): T = when (schema.type) {
      // Named
      SchemaType.RECORD -> {
        if (schema.isError) {
          ErrorType(schema)
        } else if (schema.isEmptyType) {
          EmptyType
        } else {
          RecordType(schema)
        }
      }

      SchemaType.ENUM -> EnumType(schema)
      SchemaType.FIXED -> FixedType(schema)

      // Container
      SchemaType.ARRAY -> ArrayType(schema)
      SchemaType.MAP -> MapType(schema)
      SchemaType.UNION -> {
        if (schema.isOptionalType) {
          OptionalType(schema)
          //UnionType(schema)
        } else {
          UnionType(schema)
        }
      }

      // Primitive
      SchemaType.BOOLEAN -> BooleanType(schema)
      SchemaType.BYTES -> BytesType(schema)
      SchemaType.DOUBLE -> DoubleType(schema)
      SchemaType.FLOAT -> FloatType(schema)
      SchemaType.INT -> IntType(schema)
      SchemaType.LONG -> LongType(schema)
      SchemaType.NULL -> NullType
      SchemaType.STRING -> StringType(schema)
    } as T

    /**
     * Shared equals function.
     *
     */
    internal fun AvroType.equalsFn(other: Any?): Boolean {
      // we can never equal null
      if (other == null) return false

      if (this === other) return true
      if (other !is ArrayType) return false
      if (!this.javaClass.isAssignableFrom(other.javaClass)) return false
      if (schema != other.schema) return false

      return true
    }

    internal fun AvroType.hashCodeFn(): Int = schema.hashCode()
  }

  /**
   * The schema hashCode. This includes logicaltypes and additional properties and uniquely identifies a [Schema].
   */
  override val hashCode: AvroHashCode

  /**
   * The fingerprint to identify a schema in a [org.apache.avro.message.SchemaStore]. This ignores all logicaltypes and additional properties and is not sufficient to uniquely identify a [Schema].
   */
  val fingerprint: AvroFingerprint

  /**
   * The wrapped avro [AvroSchema] of this type, including all meta-data.
   */
  val schema: AvroSchema

  val enclosedTypes: List<AvroType> get() = emptyList()
}

sealed interface WithEnclosedTypes {

  val typesMap: AvroTypesMap


}

/**
 * Marks the primitive types: BOOLEAN, BYTES, DOUBLE, FLOAT, INT, LONG, NULL, STRING.
 */
sealed interface AvroPrimitiveType : AvroType {
  val type: SchemaType
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
sealed interface AvroContainerType : AvroType, WithEnclosedTypes


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

  /**
   * If a logical type is configured in the avsc, the logical on the parsed schema
   * might be null, because the logical type is not correctly configured.
   *
   * @return `true` if logicalType is `null` but the [LogicalTypeName.PROPERTY_KEY] is set, `false` else.
   */
  val missingLogicalTypeConfig: Boolean get() = logicalType == null && logicalTypeName != null
}

interface WithDocumentation {
  val documentation: Documentation?
}

/**
 * Recursively list all subtypes of [AvroType].
 */
internal fun avroTypes(): List<KClass<out AvroType>> {
  val list = mutableListOf<KClass<out AvroType>>()

  fun addSubtypes(klass: KClass<out AvroType>) {
    klass.sealedSubclasses.forEach {
      list.add(it)
      if (it.isSealed) {
        addSubtypes(it)
      }
    }
  }

  addSubtypes(AvroType::class)

  return list
}
