package io.toolisticon.kotlin.avro.logical.conversion

import io.toolisticon.kotlin.avro.logical.conversion.parameterized.ParameterizedDoubleConversion
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ParameterizedDoubleConversionTest {

  val conversion = object : ParameterizedDoubleConversion<Long>(LogicalTypeName("bits"), Long::class.java) {

    override fun fromAvro(value: Double, schema: AvroSchema, logicalType: LogicalType?): Long {
      return value.toBits()
    }

    override fun toAvro(value: Long, schema: AvroSchema, logicalType: LogicalType?): Double {
      return Double.fromBits(value)
    }
  }

  @Test
  fun `double to bits conversion`() {

    val bits = conversion.fromAvro(1.23)
    assertThat(conversion.toAvro(bits)).isEqualTo(1.23)
  }
}
