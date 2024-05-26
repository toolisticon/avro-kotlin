package io.toolisticon.kotlin.avro.logical

import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class BuiltInLogicalTypeTest {
  @ParameterizedTest
  @CsvSource(
    value = [
      "DECIMAL,decimal,java.math.BigDecimal",
      "UUID,uuid,java.util.UUID",
      "DATE,date,java.time.LocalDate",
      "TIME_MILLIS,time-millis,java.time.LocalTime",
      "TIME_MICROS,time-micros,java.time.LocalTime",
      "TIMESTAMP_MILLIS,timestamp-millis,java.time.Instant",
      "TIMESTAMP_MICROS,timestamp-micros,java.time.Instant",
      "LOCAL_TIMESTAMP_MILLIS,local-timestamp-millis,java.time.LocalDateTime",
      "LOCAL_TIMESTAMP_MICROS,local-timestamp-micros,java.time.LocalDateTime",
      "DURATION,duration,java.time.Duration",
    ]
  )
  fun `ensure builtInTypes are configured correctly`(
    builtInLogicalType: BuiltInLogicalType,
    expectedTypeName: String,
    expectedConvertedType: Class<*>
  ) {
    assertThat(builtInLogicalType.logicalTypeName.value).isEqualTo(expectedTypeName)
    assertThat(BuiltInLogicalType.valueOfLogicalTypeName(LogicalTypeName(expectedTypeName))).isEqualTo(builtInLogicalType)
    assertThat(builtInLogicalType.convertedType).isEqualTo(expectedConvertedType)
  }

  @Test
  fun `ensure all 10 built in types exist`() {
    assertThat(BuiltInLogicalType.entries).hasSize(10)
  }

  @Test
  fun `builtInConversions contains all conversions - without duration`() {
    assertThat(BuiltInLogicalType.CONVERSIONS).hasSize(9)
  }
}

