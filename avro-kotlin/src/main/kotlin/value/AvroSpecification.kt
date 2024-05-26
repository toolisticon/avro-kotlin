package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema


/**
 * Defines valid file extension for avro declarations.
 */
enum class AvroSpecification(private val fileExtension: String) : ValueType<String> {
  SCHEMA(fileExtension = AvroSchema.FILE_EXTENSION),
  PROTOCOL(fileExtension = AvroProtocol.FILE_EXTENSION),
  ;

  companion object {
    private val BY_EXTENSION = entries.associateBy { it.value }

    val EXTENSIONS: Set<String> = BY_EXTENSION.keys

    fun valueOfExtension(extension: String) =
      requireNotNull(BY_EXTENSION[extension]) { "Not a valid extension='$extension', must be one of: ${BY_EXTENSION.keys}." }
  }

  override val value: String get() = fileExtension
}
