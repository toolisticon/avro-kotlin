package io.toolisticon.kotlin.avro.generator.spi

import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroKotlinProcessorServiceLoaderTest {

  @Test
  fun `map generated via spi contains UUID`() {
    val loader = AvroKotlinGeneratorServiceLoader.load()
    assertThat(loader.logicalTypes).containsKeys(
      BuiltInLogicalType.UUID.logicalTypeName,
      BuiltInLogicalType.DECIMAL.logicalTypeName,
      BuiltInLogicalType.TIMESTAMP_MICROS.logicalTypeName,
    )
  }
}
