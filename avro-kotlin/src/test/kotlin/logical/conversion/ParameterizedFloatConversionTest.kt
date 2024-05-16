package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.conversion.parameterized.ParameterizedFloatConversion
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ParameterizedFloatConversionTest {

  val conversion = object : ParameterizedFloatConversion<Int>(LogicalTypeName("bits"), Int::class.java) {
    override fun fromAvro(value: Float, schema: AvroSchema, logicalType: LogicalType?): Int {
      return value.toBits()
    }

    override fun toAvro(value: Int, schema: AvroSchema, logicalType: LogicalType?): Float {
      return Float.fromBits(value)
    }
  }

  @Test
  fun `float to bits conversion`() {

    val bits = conversion.fromAvro(1.23f)
    assertThat(conversion.toAvro(bits)).isEqualTo(1.23f)
  }
}
