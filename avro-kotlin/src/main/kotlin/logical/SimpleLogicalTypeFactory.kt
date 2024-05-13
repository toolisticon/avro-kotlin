package io.toolisticon.avro.kotlin.logical

import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes.LogicalTypeFactory
import org.apache.avro.Schema
import java.nio.ByteBuffer

/**
 * We need a [LogicalTypeFactory] to register custom types. It is an java avro aapi
 * requirement.
 *
 * As the [SimpleLogicalType] is de facto a singleton instance,
 * we do not really need the factory, though. We just need a "something"
 * that returns our singleton to fulfill a contract.
 *
 */
sealed class SimpleLogicalTypeFactory<
  T : SimpleLogicalType<REPRESENTATION, CONVERSION>,
  REPRESENTATION : Any,
  CONVERSION : Any>(
  val logicalType: T
) : LogicalTypeFactory {
  override fun toString(): String = toString(this::class.java.simpleName) {
    add(property = "name", value = logicalType.name, wrap = "'")
    add(property = "type", value = logicalType.type)
  }

  override fun getTypeName(): String = logicalType.getName()

  override fun fromSchema(schema: Schema?): LogicalType? = if (logicalType.name == LogicalTypeName.invoke(schema)) {
    logicalType
  } else {
    null
  }
}

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [BooleanSimpleLogicalType] when present.
 */
abstract class BooleanSimpleLogicalTypeFactory<T : BooleanSimpleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Boolean, Boolean>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [BytesSimpleLogicalType] when present.
 */
abstract class BytesSimpleLogicalTypeFactory<T : BytesSimpleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, ByteArray, ByteBuffer>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [DoubleSimpleLogicalType] when present.
 */
abstract class DoubleSimpleLogicalTypeFactory<T : DoubleSimpleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Double, Double>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [FloatSimpleLogicalType] when present.
 */
abstract class FloatSimpleLogicalTypeFactory<T : FloatSimpleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Float, Float>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [IntSimpleLogicalType] when present.
 */
abstract class IntSimpleLogicalTypeFactory<T : IntSimpleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Int, Int>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [LongSimpleLogicalType] when present.
 */
abstract class LongSimpleLogicalTypeFactory<T : LongSimpleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Long, Long>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [StringLogicalType] when present.
 */
abstract class StringSimpleLogicalTypeFactory<T : StringSimpleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, String, CharSequence>(logicalType)
