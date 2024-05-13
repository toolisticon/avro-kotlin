package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DoubleConversionTest {

  val conversion = object : DoubleConversion<Long>(LogicalTypeName("bits"), Long::class.java) {

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
