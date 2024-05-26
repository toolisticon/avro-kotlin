package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.model.SchemaType
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
value class JsonString private constructor(override val value: String) : ValueType<String> {
  companion object {
    internal val PRIMITIVE_TEMPLATE = """
      {
        "type" : %s
      }
    """.trimIndent()

    private fun schemaToString(schema: Schema): String {
      val type = SchemaType.valueOfType(schema.type)

      // a primitive without logicalType only is `"string"`
      return if (type.isPrimitive && schema.logicalType == null) {
        PRIMITIVE_TEMPLATE.format(schema.toString())
      }
      // all other schema ar valid json
      else {
        schema.toString(true)
      }
    }


    fun of(json: String) = JsonString(json.trim())

    fun of(schema: Schema) = JsonString(schemaToString(schema))

    fun of(protocol: Protocol) = JsonString(protocol.toString(true))

  }

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
