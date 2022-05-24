package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.*
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

object IoExt {


  /**
   * Default separator used in canonical name.
   */
  const val NAME_SEPARATOR = "."


  /**
   * Replace namespace dots with file-separator dash.
   */
  fun String.dotToDash(): String = replace(NAME_SEPARATOR, File.separator)

  /**
   * [Namespace] to [Path] replacing `.` with [File#separator].
   */
  fun namespaceToPath(namespace: Namespace): Path = Path(namespace.dotToDash())

  /**
   * Turns a canonical (fqn) name to a file system [Path] using suffix.
   *
   * A java class `io.acme.Foo` would become `io/acme/Foo.java`.
   */
  fun canonicalNameToPath(canonicalName: CanonicalName, fileExtension: FileExtension): Path = Path("${canonicalName.dotToDash()}.$fileExtension").normalize()

  /**
   * Creates avro file path using [Namespace], [Name] and [FileExtension].
   *
   * A `Schema(namespace=foo.bar, name=HelloWorld)` becomes `Path("foo/bar/HelloWorld.avsc")`.
   */
  fun fqnToPath(namespace: Namespace, name: Name, fileExtension: FileExtension) = canonicalNameToPath(AvroKotlinLib.canonicalName(namespace, name), fileExtension)


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
    this.writeText(content, AvroKotlinLib.UTF_8)
    return this
  }

}
