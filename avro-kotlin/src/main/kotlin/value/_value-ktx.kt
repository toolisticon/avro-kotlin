package io.toolisticon.kotlin.avro.value

import java.nio.ByteBuffer


/**
 * Marks a value type.
 */
interface ValueType<T : Any> {
  val value: T
}

interface PairType<L : Any?, R : Any?> : ValueType<Pair<L, R>>

/**
 * As Pair or Triple, put holding only one value.
 */
data class Single<T : Any>(override val value: T) : ValueType<T>

/**
 * An extended [ValueType<ByteArray>] for [ByteArray], declaring some useful extensions.
 */
interface ByteArrayValueType : ValueType<ByteArray>, WithHexString {
  /**
   * The wrapped byteArray-[value] as [HexString].
   */
  override val hex: HexString get() = HexString.of(value)

  /**
   * The wrapped byteArray-[value] as [ByteBuffer].
   */
  val buffer: ByteBuffer get() = ByteBuffer.wrap(value)

  /**
   * The size of the wrapped byteArray-[value].
   */
  val size: Int get() = value.size

  operator fun get(index: Int): Byte = value[index]
}


interface WithHexString {
  val hex: HexString
}


interface WithObjectProperties {
  val properties: ObjectProperties
}
