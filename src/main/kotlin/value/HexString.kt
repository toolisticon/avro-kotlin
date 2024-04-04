package io.toolisticon.avro.kotlin.value

import java.nio.ByteBuffer

/**
 * A String that represents a hex value.
 */
@JvmInline
value class HexString private constructor(private val single: Single<String>) : ValueType<String> by single {
  companion object {
    private const val BYTES_SIZE = 2
    private val DEFAULT_FORMAT = Triple(" ", "[", "]")

    private fun format(value: Any) = "%02X".format(value)
    private fun join(value: String) = value.chunked(2).joinToString(DEFAULT_FORMAT.first, DEFAULT_FORMAT.second, DEFAULT_FORMAT.third)


    fun intToBytes(i: Int): ByteArray = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(i).array()

    fun bytesToInt(bytes: ByteArray): Int = ByteBuffer.wrap(bytes).int
  }

  /**
   * Parses a formatted bytes-hex-string.
   */
  constructor(formatted: String) : this(
    Single(
      formatted.removePrefix("[")
        .removeSuffix("]").split(" ").joinToString("")
    )
  )

  constructor(value: Number) : this(
    single = when (value) {
      is Int -> HexString(intToBytes(value)).single
      else -> Single(format(value))
    }
  )


  /**
   * Converts a byte array into its hexadecimal string representation
   * e.g. for the V1_HEADER => [C3 01]
   *
   * @return the hexadecimal string representation of the input byte array
   */
  constructor(byteArray: ByteArray) : this(Single(byteArray.joinToString(separator = "") { format(it) }))
  constructor(byteBuffer: ByteBuffer) : this(byteBuffer.array())

  constructor(hashCode: AvroHashCode) : this(hashCode.value)
  constructor(fingerprint: AvroFingerprint) : this(fingerprint.value)

  val size: Int get() = length / BYTES_SIZE
  val length: Int get() = value.length
  val formatted: String get() = value.chunked(2).joinToString(separator = " ", prefix = "[", postfix = "]")
  val byteArray: ByteArrayValue
    get() = ByteArrayValue(value = value.chunked(2)
      .map { Integer.valueOf(it, 16) }
      .map { it.toByte() }.toByteArray()
    )

  val chunked: List<String> get() = value.chunked(BYTES_SIZE)

  fun parseInt(): Int = bytesToInt(byteArray.value)

  override fun toString() = value
}
