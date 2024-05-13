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

  override fun fromSchema(schema: Schema?): LogicalType? = if (logicalType.name == LogicalTypeName.Companion.invoke(schema)) {
    logicalType
  } else {
    null
  }
}

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [BooleanLogicalType] when present.
 */
abstract class BooleanLogicalTypeFactory<T : BooleanLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Boolean, Boolean>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [BytesLogicalType] when present.
 */
abstract class BytesLogicalTypeFactory<T : BytesLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, ByteArray, ByteBuffer>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [DoubleLogicalType] when present.
 */
abstract class DoubleLogicalTypeFactory<T : DoubleLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Double, Double>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [FloatLogicalType] when present.
 */
abstract class FloatLogicalTypeFactory<T : FloatLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Float, Float>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [IntLogicalType] when present.
 */
abstract class IntLogicalTypeFactory<T : IntLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Int, Int>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [LongLogicalType] when present.
 */
abstract class LongLogicalTypeFactory<T : LongLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, Long, Long>(logicalType)

/**
 * [SimpleLogicalTypeFactory] that returns the singleton instance of [StringLogicalType] when present.
 */
abstract class StringLogicalTypeFactory<T : StringLogicalType>(logicalType: T) : SimpleLogicalTypeFactory<T, String, CharSequence>(logicalType)
