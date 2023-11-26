package io.toolisticon.avro.kotlin.value

/**
 * Marks a value type.
 */
interface ValueType<T : Any> {
  val value: T
}

/**
 * As Pair or Triple, put holding only one value.
 */
data class Single<T : Any>(override val value: T) : ValueType<T>
