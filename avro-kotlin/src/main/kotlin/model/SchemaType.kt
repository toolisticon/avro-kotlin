package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.wrapper.TypeSupplier
import org.apache.avro.Schema.Type
import java.nio.ByteBuffer

/**
 * Wraps a [Schema#Type] for type safe access and support functions.
 */
sealed class SchemaType(
  private val type: Type
) : TypeSupplier {
  override fun get(): Type = type
  abstract val isPrimitive: Boolean
  abstract val isNamed: Boolean
  abstract val isContainer: Boolean
  val displayName: String = type.getName()

  /**
   * @param <CT> the conversion type, used by Conversion when encoding/decoding.
   * @param <RT> the representation type, used in JVM code.
   */
  sealed class PrimitiveSchemaType<REPRESENTATION : Any, CONVERSION : Any>(
    type: Type,
    val conversionType: Class<CONVERSION>,
    val representationType: Class<REPRESENTATION>
  ) : SchemaType(type) {
    override val isContainer = false
    override val isNamed = false
    override val isPrimitive = true

    fun schema() = AvroBuilder.primitiveSchema(this)
  }

  sealed class NamedSchemaType(type: Type) : SchemaType(type) {
    override val isContainer = false
    override val isNamed = true
    override val isPrimitive = false
  }

  sealed class ContainerSchemaType(type: Type) : SchemaType(type) {
    override val isContainer = true
    override val isNamed = false
    override val isPrimitive = false
  }

  data object ENUM : NamedSchemaType(type = Type.ENUM)
  data object FIXED : NamedSchemaType(type = Type.FIXED)
  data object RECORD : NamedSchemaType(type = Type.RECORD)

  data object ARRAY : ContainerSchemaType(type = Type.ARRAY)
  data object MAP : ContainerSchemaType(type = Type.MAP)
  data object UNION : ContainerSchemaType(type = Type.UNION)


  data object BOOLEAN : PrimitiveSchemaType<Boolean, Boolean>(
    type = Type.BOOLEAN,
    conversionType = Boolean::class.java,
    representationType = Boolean::class.java
  )

  data object BYTES : PrimitiveSchemaType<ByteArray, ByteBuffer>(
    type = Type.BYTES,
    conversionType = ByteBuffer::class.java,
    representationType = ByteArray::class.java
  )

  data object DOUBLE : PrimitiveSchemaType<Double, Double>(
    type = Type.DOUBLE,
    conversionType = Double::class.java,
    representationType = Double::class.java
  )

  data object FLOAT : PrimitiveSchemaType<Float, Float>(
    type = Type.FLOAT,
    conversionType = Float::class.java,
    representationType = Float::class.java
  )

  data object INT : PrimitiveSchemaType<Int, Int>(
    type = Type.INT,
    conversionType = Int::class.java,
    representationType = Int::class.java
  )

  data object LONG : PrimitiveSchemaType<Long, Long>(
    type = Type.LONG,
    conversionType = Long::class.java,
    representationType = Long::class.java
  )

  data object NULL : PrimitiveSchemaType<Nothing, Nothing>(
    type = Type.NULL,
    conversionType = Nothing::class.java,
    representationType = Nothing::class.java
  )

  data object STRING : PrimitiveSchemaType<String, CharSequence>(
    type = Type.STRING,
    conversionType = CharSequence::class.java,
    representationType = String::class.java
  )

  companion object {

    internal val PRIMITIVE_TYPES: Set<SchemaType> get() = setOf(BOOLEAN, BYTES, DOUBLE, FLOAT, INT, LONG, NULL, STRING)
    internal val NAMED_TYPES: Set<SchemaType> get() = setOf(ENUM, FIXED, RECORD)
    internal val CONTAINER_TYPES: Set<SchemaType> get() = setOf(ARRAY, MAP, UNION)

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
