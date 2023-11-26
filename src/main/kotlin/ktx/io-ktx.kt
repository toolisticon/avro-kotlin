package io.toolisticon.avro.kotlin.ktx

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.NAME_SEPARATOR
import io.toolisticon.avro.kotlin._bak.GenericAvroDeclarationFqn
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Replace path file-separator dash with namespace dots.
 */
fun String.dashToDot(): String = replace(File.separator, NAME_SEPARATOR)



/**
 * Creates avro file path using [Namespace], [Name] and [FileExtension].
 *
 * A `Schema(namespace=foo.bar, name=HelloWorld)` becomes `Path("foo/bar/HelloWorld.avsc")`.
 */
fun fqnToPath(namespace: Namespace, name: Name, fileExtension: String) = GenericAvroDeclarationFqn(namespace, name, fileExtension).path


/**
 * Takes a directory and extends it with a given path to get a file path.
 */
fun File.file(path: Path): Path = toPath().resolve(path)

/**
 * Write content to given file, creates directory path if it not exists.
 */
fun File.writeText(content: String): File {
  if (!this.parentFile.exists()) {
    this.parentFile.mkdirs()
  }
  this.writeText(content, AvroKotlin.Constants.UTF_8)
  return this
}
