package io.toolisticon.kotlin.avro.generator.api.spi

interface AvroKotlinGeneratorSpi {
  companion object Order {
    const val DEFAULT_ORDER = 0
  }

  val order: Int
}
