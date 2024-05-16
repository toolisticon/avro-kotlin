package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.wrapper.TypeSupplier
import org.apache.avro.Schema.Type
import kotlin.reflect.KClass

/**
 * Wraps a [Schema#Type] for type safe access and support functions.
 */
sealed class SchemaType(
  private val type: Type,
  val isPrimitive: Boolean,
  val isNamed: Boolean,
  val isContainer: Boolean,
) : TypeSupplier {
  override fun get(): Type = type
  val displayName: String = type.getName()

  data object ENUM : NamedSchemaType(type = Type.ENUM)
  data object FIXED : NamedSchemaType(type = Type.FIXED)
  data object RECORD : NamedSchemaType(type = Type.RECORD)

  data object ARRAY : ContainerSchemaType(type = Type.ARRAY)
  data object MAP : ContainerSchemaType(type = Type.MAP)
  data object UNION : ContainerSchemaType(type = Type.UNION)


  data object BOOLEAN : PrimitiveSchemaTypeForLogicalType<Boolean>(
    type = Type.BOOLEAN,
    jvmType = Boolean::class
  )

  data object BYTES : PrimitiveSchemaTypeForLogicalType<ByteArray>(
    type = Type.BYTES,
    jvmType = ByteArray::class
  )

  data object DOUBLE : PrimitiveSchemaTypeForLogicalType<Double>(
    type = Type.DOUBLE,
    jvmType = Double::class
  )

  data object FLOAT : PrimitiveSchemaTypeForLogicalType<Float>(
    type = Type.FLOAT,
    jvmType = Float::class
  )

  data object INT : PrimitiveSchemaTypeForLogicalType<Int>(
    type = Type.INT,
    jvmType = Int::class
  )

  data object LONG : PrimitiveSchemaTypeForLogicalType<Long>(
    type = Type.LONG,
    jvmType = Long::class
  )

  data object NULL : PrimitiveSchemaType(type = Type.NULL)

  data object STRING : PrimitiveSchemaTypeForLogicalType<String>(
    type = Type.STRING,
    jvmType = String::class
  )

  companion object {

    internal val PRIMITIVE_TYPES: Set<PrimitiveSchemaType> get() = setOf(BOOLEAN, BYTES, DOUBLE, FLOAT, INT, LONG, NULL, STRING)
    internal val NAMED_TYPES: Set<NamedSchemaType> get() = setOf(ENUM, FIXED, RECORD)
    internal val CONTAINER_TYPES: Set<ContainerSchemaType> get() = setOf(ARRAY, MAP, UNION)

    // cannot be null
    fun valueOfType(type: Type): SchemaType = when (type) {
      Type.RECORD -> RECORD
      Type.ENUM -> ENUM
      Type.ARRAY -> ARRAY
      Type.MAP -> MAP
      Type.UNION -> UNION
      Type.FIXED -> FIXED
      Type.STRING -> STRING
      Type.BYTES -> BYTES
      Type.INT -> INT
      Type.LONG -> LONG
      Type.FLOAT -> FLOAT
      Type.DOUBLE -> DOUBLE
      Type.BOOLEAN -> BOOLEAN
      Type.NULL -> NULL
    }
  }
}

sealed class NamedSchemaType(type: Type) : SchemaType(
  type = type,
  isContainer = false,
  isNamed = true,
  isPrimitive = false
)

sealed class ContainerSchemaType(type: Type) : SchemaType(
  type = type,
  isContainer = true,
  isNamed = false,
  isPrimitive = false
)

sealed class PrimitiveSchemaType(type: Type) : SchemaType(
  type = type,
  isContainer = false,
  isNamed = false,
  isPrimitive = true
)

sealed class PrimitiveSchemaTypeForLogicalType<JVM_TYPE : Any>(
  type: Type,
  val jvmType: KClass<JVM_TYPE>
) : PrimitiveSchemaType(type) {

  fun schema() = AvroBuilder.primitiveSchema(this)
}
