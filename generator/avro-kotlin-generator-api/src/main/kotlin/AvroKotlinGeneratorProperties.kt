package io.toolisticon.kotlin.avro.generator.api

data class AvroKotlinGeneratorProperties(

  /**
   * You can decide to add a suffix to the root data class file
   * that is generated. Useful to create java and kotlin files in the same project
   * to avoid classpath conflicts.
   */
  val schemaTypeSuffix: String = "",


  /**
   * Flag indicating if the `SuppressRedundantModifiers` annotation should be added to the file.
   */
  val suppressRedundantModifiers: Boolean = true
) {


}
