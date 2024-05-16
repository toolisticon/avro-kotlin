package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.conversion.parameterized.ParameterizedBooleanConversion
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ParameterizedBooleanConversionTest {
  private val name = LogicalTypeName("boolean")

  @Test
  fun `boolean as int 0 or 1`() {
    val conversion = object : ParameterizedBooleanConversion<Int>(name, Int::class.java) {

      override fun fromAvro(value: Boolean, schema: AvroSchema, logicalType: LogicalType?): Int = if (value) 1 else 0

      override fun toAvro(value: Int, schema: AvroSchema, logicalType: LogicalType?): Boolean = if (value == 1) true else false
    }

    val b = conversion.toAvro(0)

    assertThat(conversion.fromAvro(b)).isEqualTo(0)
  }
}
