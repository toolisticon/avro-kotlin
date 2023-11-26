package io.toolisticon.avro.kotlin.value

/**
 * Defines valid file extension for avro declarations.
 */
enum class AvroSpecification(private val fileExtension: String) : ValueType<String> {
  SCHEMA(fileExtension = "avsc"),
  PROTOCOL(fileExtension = "avpr"),
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
