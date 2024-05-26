package io.toolisticon.kotlin.avro.logical.conversion

import io.toolisticon.kotlin.avro.TestFixtures
import io.toolisticon.kotlin.avro.logical.conversion.parameterized.ParameterizedBytesConversion
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.ByteArrayValue
import io.toolisticon.kotlin.avro.value.HexString
import io.toolisticon.kotlin.avro.value.LogicalTypeName
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
      val hex = HexString.parse(value)
      return ByteArrayValue.parse(hex).value
    }
  }

  @Test
  fun `bytes to string`() {
    val bytes = conversion.toAvro(TestFixtures.SINGLE_STRING_ENCODED)

    assertThat(conversion.fromAvro(bytes)).isEqualTo(TestFixtures.SINGLE_STRING_ENCODED)
  }
}
