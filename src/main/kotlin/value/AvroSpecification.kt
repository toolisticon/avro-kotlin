package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.FileExtension

/**
 * Defines valid file extension for avro declarations.
 */
enum class AvroSpecification(private val fileExtension: String) : ValueType<String> {
  SCHEMA(fileExtension = FileExtension.SCHEMA),
  PROTOCOL(fileExtension = FileExtension.PROTOCOL),
  ;

  companion object {
    private val BY_EXTENSION = entries.associateBy { it.value }

    val EXTENSIONS = BY_EXTENSION.keys

    fun valueOfExtension(extension: String) =
      requireNotNull(BY_EXTENSION[extension]) { "Not a valid extension='$extension', must be one of: ${BY_EXTENSION.keys}." }
  }

  override val value: String get() = fileExtension

  override fun toString() = value
}
