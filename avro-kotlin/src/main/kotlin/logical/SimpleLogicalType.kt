package io.toolisticon.avro.kotlin.logical

import _ktx.StringKtx
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import java.nio.ByteBuffer

/**
 * A [SimpleLogicalType] just defines the name and the schema type it is applicable
 * for. A simple logical type must _never_ require additional property parameters (decimal
 * with scale and precision is _not_ a simple logical type).
 *
 * A [SimpleLogicalType] is best implemented as (data) Object, as it is de facto a singleton.
 */
sealed class SimpleLogicalType<REPRESENTATION : Any, CONVERSION : Any>(
  val name: LogicalTypeName,
  val type: SchemaType.PrimitiveSchemaType<REPRESENTATION, CONVERSION>
) : LogicalType(name.value) {

  override fun toString() = StringKtx.toString(this::class.java.simpleName) {
    add(property = "name", value = name, wrap = "'")
    add("type", type)
  }

  fun schema() = AvroSchema(addToSchema(type.schema().get()))

  override fun addToSchema(schema: Schema): Schema {
    return super.addToSchema(schema)
  }

  override fun getName(): String = name.value

  override fun validate(schema: Schema) {
    super.validate(schema)
    require(SchemaType.valueOfType(schema.type) == type) { "$this is only valid for $type." }
  }
}

/**
 * [SimpleLogicalType] for [SchemaType.BOOLEAN], must not require additional properties and should be implemented as singleton.
 */
abstract class BooleanLogicalType(name: LogicalTypeName) : SimpleLogicalType<Boolean, Boolean>(name, SchemaType.BOOLEAN)

/**
 * [SimpleLogicalType] for [SchemaType.BYTES], must not require additional properties and should be implemented as singleton.
 */
abstract class BytesLogicalType(name: LogicalTypeName) : SimpleLogicalType<ByteArray, ByteBuffer>(name, SchemaType.BYTES)

/**
 * [SimpleLogicalType] for [SchemaType.DOUBLE], must not require additional properties and should be implemented as singleton.
 */
abstract class DoubleLogicalType(name: LogicalTypeName) : SimpleLogicalType<Double, Double>(name, SchemaType.DOUBLE)

/**
 * [SimpleLogicalType] for [SchemaType.FLOAT], must not require additional properties and should be implemented as singleton.
 */
abstract class FloatLogicalType(name: LogicalTypeName) : SimpleLogicalType<Float, Float>(name, SchemaType.FLOAT)

/**
 * [SimpleLogicalType] for [SchemaType.INT], must not require additional properties and should be implemented as singleton.
 */
abstract class IntLogicalType(name: LogicalTypeName) : SimpleLogicalType<Int, Int>(name, SchemaType.INT)

/**
 * [SimpleLogicalType] for [SchemaType.LONG], must not require additional properties and should be implemented as singleton.
 */
abstract class LongLogicalType(name: LogicalTypeName) : SimpleLogicalType<Long, Long>(name, SchemaType.LONG)

/**
 * [SimpleLogicalType] for [SchemaType.STRING], must not require additional properties and should be implemented as singleton.
 */
abstract class StringLogicalType(name: LogicalTypeName) : SimpleLogicalType<String, CharSequence>(name, SchemaType.STRING)
