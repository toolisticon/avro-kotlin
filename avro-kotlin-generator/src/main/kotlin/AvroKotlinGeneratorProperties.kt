package io.toolisticon.kotlin.avro.generator

import java.time.Instant

interface AvroKotlinGeneratorProperties {

  /**
   * You can decide to add a suffix to the root data class file
   * that is generated. Useful to create java and kotlin files in the same project
   * to avoid classpath conflicts.
   */
  val schemaTypeSuffix: String get() = ""

  /**
   * Flag indicating if the `SuppressRedundantModifiers` annotation should be added to the file.
   */
  val suppressRedundantModifiers: Boolean get() = true

  /**
   * Supplier for `Instant.now()`, can be overwritten for testing.
   */
  val nowSupplier: () -> Instant get() = { Instant.now() }
}

/**
 * Configuration properties for the generator. Could be created  manually,
 * read from yaml file or be defined in plugin configuration.
 */
data class DefaultAvroKotlinGeneratorProperties(

  /**
   * You can decide to add a suffix to the root data class file
   * that is generated. Useful to create java and kotlin files in the same project
   * to avoid classpath conflicts.
   */
  override val schemaTypeSuffix: String = "",


  /**
   * Flag indicating if the `SuppressRedundantModifiers` annotation should be added to the file.
   */
  override val suppressRedundantModifiers: Boolean = true,

  /**
   * Supplier for `Instant.now()`, can be overwritten for testing.
   */
  override val nowSupplier: () -> Instant = { Instant.now() }
) : AvroKotlinGeneratorProperties
