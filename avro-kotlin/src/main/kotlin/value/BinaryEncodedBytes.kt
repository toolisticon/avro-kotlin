package io.toolisticon.kotlin.avro.value

import java.util.function.Supplier

/**
 * The encoded message. This is only the payload data,
 * so no marker header and encoded schemaId are present.
 */
@JvmInline
value class BinaryEncodedBytes(override val value: ByteArray) : ByteArrayValueType, Supplier<ByteArray> {

  override fun get(): ByteArray = value

  override fun toString() = hex.formatted
}
