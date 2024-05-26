package io.toolisticon.kotlin.avro.logical.conversion

import io.toolisticon.kotlin.avro.logical.conversion.parameterized.ParameterizedBooleanConversion
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
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
