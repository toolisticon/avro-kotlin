package io.toolisticon.avro.kotlin.value

import java.nio.ByteBuffer

@JvmInline
value class ByteArrayValue(override val value: ByteArray) : ValueType<ByteArray>, WithHexString {

  constructor(hex: String) : this(HexString(hex))
  constructor(hex: HexString) : this(hex.byteArray.value)
  constructor(buffer: ByteBuffer) : this(buffer.array())

  /**
   * Converts a byte array into its hexadecimal string representation
   * e.g. for the V1_HEADER => [C3 01]
   * @return the hexadecimal string representation of the input byte array
   */
  override val hex: HexString get() = HexString(value)
  val formatted: String get() = hex.formatted
  val buffer: ByteBuffer get() = ByteBuffer.wrap(value)
  val size: Int get() = value.size

  /**
   * Splits an array in `n+1` parts, where `n` is the number of given indexes.
   * The first slice reaches from 0 to index_0, the last slice from index_n to end.
   *
   * @param indexes - positive ints, must be sorted
   * @return list of `n+1` byte arrays, each having a size of the diff between the indexes.
   */
  fun split(vararg indexes: Int): List<ByteArrayValue> {
    require(indexes.none { it < 0 || it > size - 1 }) { "all indexes have to match '0 < index < size-1', was: indexes=${indexes.toList()}, size=$size" }
    require(indexes.toList() == indexes.sorted()) { "indexes must be ordered, was: ${indexes.toList()}" }
    require(indexes.size == indexes.distinct().size) { "indexes must be unique, was: ${indexes.toList()}" }

    val allButLast =
      indexes.fold(0 to emptyList<ByteArray>()) { pair, nextIndex -> nextIndex to pair.second + value.copyOfRange(pair.first, nextIndex) }

    return (allButLast.second + value.copyOfRange(allButLast.first, size)).map { ByteArrayValue(it) }
  }

  /**
   * Reads [size] bytes from given buffer, starting at given position.
   * Ensures that the original buffer position is kept.
   *
   * @param position - the position to start reading
   * @param size - how many bytes should be read. if not given, all remaining bytes are read
   * @return bytesArray containg [size] bytes starting at [position]
   */
  fun extract(position: Int, size: Int? = null): ByteArrayValue = with(buffer) {
    this.position(position)
    require(size == null || size > 0) { "size < 1: ($size < 1)" }
    val maxSize = remaining()
    require(size == null || size <= maxSize) { "Cannot extract from position=$position, size=$size, remaining=${this.remaining()}" }

    val bytes = ByteArray(size ?: this.remaining())
    this.get(bytes)
    ByteArrayValue(bytes)
  }

  fun contentEquals(other: ByteArrayValue) = this.value.contentEquals(other.value)

  override fun toString() = hex.formatted
}
