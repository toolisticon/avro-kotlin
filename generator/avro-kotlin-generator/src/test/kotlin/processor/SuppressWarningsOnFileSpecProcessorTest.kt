package io.toolisticon.kotlin.avro.generator.processor

import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class SuppressWarningsOnFileSpecProcessorTest {

  @Test
  fun `add single suppression`() {
    val processor = SuppressWarningsOnFileSpecProcessor(suppressions = listOf("Hello"))

    val file = KotlinFileSpecBuilder.builder("foo", "Bar")
      .apply {
        processor.addSuppressAnnotation(
          AvroKotlinGeneratorProperties(suppressRedundantModifiers = true),
          this
        )
      }.build()

    assertThat(file.code.trim().lines().first()).isEqualTo(
      """@file:Suppress("Hello")"""
    )
  }

  @Test
  fun `add multiple suppressions`() {
    val processor = SuppressWarningsOnFileSpecProcessor(suppressions = listOf("Hello", "World"))

    val file = KotlinFileSpecBuilder.builder("foo", "Bar")
      .apply {
        processor.addSuppressAnnotation(
          AvroKotlinGeneratorProperties(suppressRedundantModifiers = true),
          this
        )
      }.build()

    assertThat(file.code.trim().lines().first()).isEqualTo(
      """@file:Suppress("Hello", "World")"""
    )
  }
}
