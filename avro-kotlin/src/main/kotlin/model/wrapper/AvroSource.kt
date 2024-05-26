package io.toolisticon.kotlin.avro.model.wrapper

import io.toolisticon.kotlin.avro.value.AvroSpecification
import io.toolisticon.kotlin.avro.value.JsonString
import java.io.File
import java.net.URL

/**
 * The source of a current parse operation.
 *
 * Can be:
 *
 * * [File],
 * * resource [URL]
 * * [JsonString] directly.
 */
sealed interface AvroSource {
  val json: JsonString
  val specification: AvroSpecification
}

data class ResourceSource(
  override val json: JsonString,
  override val specification: AvroSpecification,
  val url: URL
) : AvroSource

data class FileSource(
  override val json: JsonString,
  override val specification: AvroSpecification,
  val file: File
) : AvroSource

data class JsonSource(
  override val json: JsonString,
  override val specification: AvroSpecification
) : AvroSource
