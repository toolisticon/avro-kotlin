package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.PRIMITIVE_TYPES
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.InputStream

/**
 * Represents valid json.
 *
 * Deals with shortcomings of avro-java where primitive schemas without logical-types
 * do not represent to valid json ... which changes once they have a logical type ...
 */
@JvmInline
value class JsonString
private constructor(
  /**
   * We have tro wrap string here to remain immutable and still allow trimming the giving String value.
   */
  private val single: Single<String>
) : ValueType<String> by single {
  companion object {
    private val primitiveTemplate = """
      {
        "type" : %s
      }
    """.trimIndent()

    private fun schemaToString(schema: Schema) = // a primitive without logicalType only is `"string"`
      if (PRIMITIVE_TYPES.contains(schema.type) && schema.logicalType == null) {
        primitiveTemplate.format(schema.toString())
      }
      // all other schema ar valid json
      else {
        schema.toString(true)
      }
  }

  constructor(json: String) : this(Single(json.trim()))

  constructor(schema: Schema) : this(schemaToString(schema))

  constructor(protocol: Protocol) : this(protocol.toString(true).trim())

  init {
    val isValidJson = arrayOf("{" to "}", "[" to "]").any {
      value.startsWith(it.first) && value.endsWith(it.second)
    }
    require(isValidJson) {
      "Valid json must at least start/end with '{}' or '[]', was '$value'."
    }
  }

  fun inputStream(): InputStream = value.byteInputStream(AvroKotlin.UTF_8)

  override fun toString() = value
}
