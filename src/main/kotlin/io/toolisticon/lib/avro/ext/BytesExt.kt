package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.AvroKotlinLib

import java.nio.ByteBuffer
import java.nio.ByteOrder

object BytesExt {

  /**
   * Converts a byte array into its hexadecimal string representation
   * e.g. for the V1_HEADER => [C3 01]
   *
   * @param separator - what to print between the bytes, defaults to " "
   * @param prefix - start of string, defaults to "["
   * @param suffix - end of string, defaults to "]"
   * @return the hexadecimal string representation of the input byte array
   */
  fun ByteArray.toHexString(): String = this.joinToString(
    separator = " ",
    prefix = "[",
    postfix = "]"
  ) { "%02X".format(it) }

  /**
   * @return [ByteBuffer.wrap] for given array
   */
  fun ByteArray.buffer(): ByteBuffer = ByteBuffer.wrap(this)

  /**
   * Splits an array in `n+1` parts, where `n` is the number of given indexes.
   * The first slice reaches from 0 to index_0, the last slice from index_n to end.
   *
   * @param indexes - positive ints, must be sorted
   * @return list of `n+1` byte arrays, each having a size of the diff between the indexes.
   */
  fun ByteArray.split(vararg indexes: Int): List<ByteArray> {
    require(indexes.none { it < 0 || it > size - 1 }) { "all indexes have to match '0 < index < size-1', was: indexes=${indexes.toList()}, size=$size" }
    require(indexes.toList() == indexes.sorted()) { "indexes must be ordered, was: ${indexes.toList()}" }
    require(indexes.size == indexes.distinct().size) { "indexes must be unique, was: ${indexes.toList()}" }

    val allButLast =
      indexes.fold(0 to emptyList<ByteArray>()) { pair, nextIndex -> nextIndex to pair.second + this.copyOfRange(pair.first, nextIndex) }
    return allButLast.second + this.copyOfRange(allButLast.first, size)
  }

  /**
   * @see ByteArray.split(indexes)
   */
  fun ByteBuffer.split(vararg indexes: Int): List<ByteArray> = this.array().split(*indexes)

  /**
   * Reads [size] bytes from given buffer, starting at given position.
   * Ensures that the original buffer position is kept.
   *
   * @param position - the position to start reading
   * @param size - how many bytes should be read. if not given, all remaining bytes are read
   * @return bytesArray containg [size] bytes starting at [position]
   */
  fun ByteBuffer.extract(position: Int, size: Int? = null): ByteArray {
    val originalPosition = this.position()
    try {
      this.position(position)
      require(size == null || size > 0) { "size < 1: ($size < 1)" }
      val maxSize = remaining()
      require(size == null || size <= maxSize) { "Cannot extract from position=$position, size=$size, remaining=${this.remaining()}" }

      val bytes = ByteArray(size ?: this.remaining())
      this.get(bytes)
      return bytes
    } finally {
      this.position(originalPosition)
    }
  }

  fun ByteArray.readLong(): Long {
    require(this.size == Long.SIZE_BYTES) { "Size must be exactly Long.SIZE_BYTES (${Long.SIZE_BYTES}." }
    return this.buffer().order(ByteOrder.LITTLE_ENDIAN).long
  }

  fun ByteBuffer.isAvroSingleObjectEncoded(): Boolean = extract(0, AvroKotlinLib.AVRO_V1_HEADER.size).contentEquals(AvroKotlinLib.AVRO_V1_HEADER)

  fun ByteArray.isAvroSingleObjectEncoded(): Boolean = buffer().isAvroSingleObjectEncoded()

}
