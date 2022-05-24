package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.AvroKotlinLib.DEFAULT_CLASS_LOADER
import io.toolisticon.lib.avro.Name
import io.toolisticon.lib.avro.Namespace
import io.toolisticon.lib.avro.io.file
import io.toolisticon.lib.avro.io.parseFromResource
import org.apache.avro.Schema
import java.io.File

/**
 * Represents a (json) avro [Schema] file. Based on this information the file can be read from resource and read/written from/to a file.
 */
data class SchemaFqn(override val namespace: Namespace, override val name: Name) :
  AbstractAvroDeclarationFqn(namespace = namespace, name = name, fileExtension = AvroKotlinLib.EXTENSION_SCHEMA) {
  companion object {
    fun AvroFqn.schemaFqn() = AvroKotlinLib.schema(this)
  }

  override fun toString(): String = super.toString()
}


/**
 * Load from resource.
 *
 * @param prefix optional path inside the classpath if not in root
 * @param classLoader optional -  if specific one is required, otherwise classloader of [AvroKotlinLib] is used
 * @return parsed avro schema  instance
 */
fun SchemaFqn.fromResource(
  prefix: String? = null,
  classLoader: ClassLoader = DEFAULT_CLASS_LOADER
): Schema = parseFromResource(
  namespace = namespace,
  name = name,
  fileExtension = this.fileExtension,
  classLoader = classLoader,
  prefix = prefix
) { Schema.Parser().parse(it) }

/**
 * Read [Schema] from directory assuming file has canonical path.
 */
fun SchemaFqn.fromDirectory(dir: File) : Schema = Schema.Parser().parse(dir.file(path).toFile())
