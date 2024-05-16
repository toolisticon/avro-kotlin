package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.TestFixtures
import io.toolisticon.avro.kotlin.logical.conversion.parameterized.ParameterizedBytesConversion
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.ByteArrayValue
import io.toolisticon.avro.kotlin.value.HexString
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ParameterizedBytesConversionTest {
  private val name = LogicalTypeName("bytes")

  private val conversion = object : ParameterizedBytesConversion<String>(name, String::class.java) {

    override fun fromAvro(value: ByteArray, schema: AvroSchema, logicalType: LogicalType?): String {
      return ByteArrayValue(value).hex.formatted
    }

    override fun toAvro(value: String, schema: AvroSchema, logicalType: LogicalType?): ByteArray {
      val hex = HexString(value)
      return ByteArrayValue(hex).value
    }
  }

  @Test
  fun `bytes to string`() {
    val bytes = conversion.toAvro(TestFixtures.SINGLE_STRING_ENCODED)

    assertThat(conversion.fromAvro(bytes)).isEqualTo(TestFixtures.SINGLE_STRING_ENCODED)
  }
}
