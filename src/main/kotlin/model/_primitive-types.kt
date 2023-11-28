package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.nullableToString
import io.toolisticon.avro.kotlin.AvroKotlin.logicalTypeName
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import io.toolisticon.avro.kotlin.value.Name
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import org.apache.avro.Schema.Type

/**
 * A binary value.
 */
@JvmInline
value class BooleanType(override val schema: AvroSchema) : AvroPrimitiveType, SchemaSupplier by schema {

  init {
    require(schema.isBooleanType) { "Not a BOOLEAN type." }
  }

  override val type: Type get() = Type.BOOLEAN
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  override fun toString() = schema.name.toString()
}

/**
 * Sequence of 8-bit unsigned bytes.
 */
@JvmInline
value class BytesType(override val schema: AvroSchema) : AvroPrimitiveType, WithLogicalType, SchemaSupplier by schema {

  init {
    require(schema.isBytesType) { "Not a BYTES type." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val type: Type get() = Type.BYTES
  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = logicalTypeName(logicalType)

  override fun toString() = schema.name.toString().removeSuffix(")") +
    logicalType?.name.nullableToString(", logicalType='", suffix = "'") +
    ")"
}

/**
 * Double precision (64-bit) IEEE 754 floating-point number
 */
@JvmInline
value class DoubleType(override val schema: AvroSchema) : AvroPrimitiveType, SchemaSupplier by schema {
  init {
    require(schema.isDoubleType) { "Not a DOUBLE type." }
  }

  override val type: Type get() = Type.DOUBLE

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  override fun toString() = schema.name.toString()
}

/**
 * Single precision (32-bit) IEEE 754 floating-point number.
 */
@JvmInline
value class FloatType(override val schema: AvroSchema) : AvroPrimitiveType, SchemaSupplier by schema {

  init {
    require(schema.isFloatType) { "Not a FLOAT type." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val type: Type get() = Type.FLOAT

  override fun toString() = schema.name.toString()
}

/**
 * 32-bit signed integer.
 */
@JvmInline
value class IntType(override val schema: AvroSchema) : AvroPrimitiveType, WithLogicalType, SchemaSupplier by schema {
  init {
    require(schema.isIntType) { "Not an INT type." }
  }

  override val type: Type get() = schema.type
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = logicalTypeName(logicalType)

  override fun toString() = schema.name.toString().removeSuffix(")") +
    logicalType?.name.nullableToString(", logicalType='", suffix = "'") +
    ")"
}

/**
 * 64-bit signed integer.
 */
@JvmInline
value class LongType(override val schema: AvroSchema) : AvroPrimitiveType, WithLogicalType, SchemaSupplier by schema {
  init {
    require(schema.isLongType) { "Not a LONG type." }
  }

  override val type: Type get() = Type.LONG

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = logicalTypeName(logicalType)

  override fun toString() = schema.name.toString().removeSuffix(")") + logicalType?.name.nullableToString(", logicalType='", suffix = "'") + ")"
}

/**
 * Null - no value.
 */
data object NullType : AvroPrimitiveType {

  override val schema = primitiveSchema(type = Type.NULL)
  override fun get(): Schema = schema.get()

  override val type: Type = schema.type
  override val name: Name = schema.name
  override val hashCode: AvroHashCode = schema.hashCode
  override val fingerprint: AvroFingerprint = schema.fingerprint

  init {
    require(type == Type.NULL) { "not NULL type." }
  }
}

/**
 * Unicode character sequence
 */
@JvmInline
value class StringType(override val schema: AvroSchema) : AvroPrimitiveType, WithLogicalType, SchemaSupplier by schema {
  init {
    require(schema.isStringType) { "Not a STRING type." }
  }

  override val type: Type get() = Type.STRING
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = logicalTypeName(logicalType)

  override fun toString() = schema.name.toString().removeSuffix(")") +
    logicalType?.name.nullableToString(", logicalType='", suffix = "'") +
    ")"
}
