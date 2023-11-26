package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.DEFAULT_CLASS_LOADER
import io.toolisticon.avro.kotlin.ktx.file
import io.toolisticon.avro.kotlin.ktx.fqn
import io.toolisticon.avro.kotlin.ktx.parseFromResource
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.Schema
import java.io.File
import java.io.FileNotFoundException

/**
 * Represents a (json) avro [Schema] file. Based on this information the file can be read from resource and read/written from/to a file.
 */
@Deprecated("remove")
data class SchemaFqn(override val namespace: Namespace, override val name: Name) :
  GenericAvroDeclarationFqn(namespace = namespace, name = name, fileExtension = AvroKotlin.Constants.EXTENSION_SCHEMA) {

  override fun toString(): String = super.toString()
}


/**
 * Load from resource.
 *
 * @param prefix optional path inside the classpath if not in root
 * @param classLoader optional -  if specific one is required, otherwise classloader of [AvroKotlin] is used
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
fun SchemaFqn.fromDirectory(dir: File, failOnFqnMismatch: Boolean = true): Schema {
  val avscFile: File = dir.file(path).toFile()
  if (!avscFile.exists() || avscFile.isDirectory) {
    throw FileNotFoundException("could not read from file=$avscFile")
  }

  val schema: Schema = Schema.Parser().parse(avscFile)

  if (failOnFqnMismatch) {
    val schemaFqn = schema.fqn()
    if (schemaFqn != this) {
      throw AvroDeclarationMismatchException(schemaFqn, this)
    }
  }

  return schema
}
