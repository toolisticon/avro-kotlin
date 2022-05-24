package io.toolisticon.lib.avro.io

import io.toolisticon.lib.avro.*
import io.toolisticon.lib.avro.AvroKotlinLib.NAME_SEPARATOR
import io.toolisticon.lib.avro.AvroKotlinLib.UTF_8
import io.toolisticon.lib.avro.AvroKotlinLib.canonicalName
import org.apache.avro.JsonProperties
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.Path

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
fun fqnToPath(namespace: Namespace, name: Name, fileExtension: FileExtension) = canonicalNameToPath(canonicalName(namespace, name), fileExtension)

/**
 * URL of resource using path based on namespace and name.
 *
 * @param namespace the namespace (aka directory path to look in)
 * @param name of the file to load
 * @param fileExtension file extension
 * @param prefix optional prefix-path if resource is not under root in classpath
 * @param classLoader optional classloader if not given, the classloader of AvroKotlinLib is used
 * @return URL pointing to resource.
 */
fun resource(namespace: Namespace, name: Name, fileExtension: FileExtension, prefix: String? = null, classLoader: ClassLoader = AvroKotlinLib.DEFAULT_CLASS_LOADER): URL {
  val path = canonicalNameToPath(canonicalName(namespace, name), fileExtension)

  val resource = (if (prefix != null) {
    "$prefix${File.separator}$path"
  } else {
    path.toString()
  }).removePrefix("/")

  return requireNotNull(classLoader.getResource(resource)) { "resource not found: $path" }
}

/**
 * Gets [resource] URL and uses the given parser function to create either a [Schema] or [Protocol] instance.
 *
 * @param namespace namespace aka directory path
 * @param name the file name
 * @param fileExtension the file extension
 * @param prefix optional path if not in resources root
 * @param classLoader optional ClassLoader, default is [AvroKotlinLib#DEFAULT_CLASS_LOADER]
 * @param parser parser function, either Schema.Parser() or Protocol.parse()
 */
fun <T : JsonProperties> parseFromResource(
  namespace: Namespace,
  name: Name,
  fileExtension: FileExtension,
  prefix: String? = null,
  classLoader: ClassLoader = AvroKotlinLib.DEFAULT_CLASS_LOADER,
  parser: (InputStream) -> T
): T {
  return resource(namespace, name, fileExtension, prefix, classLoader).openStream().use { parser.invoke(it) }
}

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
  this.writeText(content, UTF_8)
  return this
}
