package io.toolisticon.avro.kotlin.ktx

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin._bak.GenericAvroDeclarationFqn
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.JsonProperties
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import kotlin.io.path.Path

fun loadResource(name: String): String = with(name.trailingSlash()) {
  return resourceUrl(this).readText()
}

fun resourceUrl(resource: String): URL = requireNotNull(
  {}::class.java.getResource(resource.trailingSlash())
) { "resource not found: $resource" }


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
fun resource(
  namespace: Namespace,
  name: Name,
  fileExtension: String,
  prefix: String? = null,
  classLoader: ClassLoader = AvroKotlin.Constants.DEFAULT_CLASS_LOADER
): URL {
  val fqn = GenericAvroDeclarationFqn(namespace, name, fileExtension)
  val path = fqn.path

  val resource: String = (if (prefix != null) {
    "$prefix${File.separator}$path"
  } else {
    path.toString()
  }).removePrefix(File.separator)

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
  fileExtension: String,
  prefix: String? = null,
  classLoader: ClassLoader = AvroKotlin.Constants.DEFAULT_CLASS_LOADER,
  parser: (InputStream) -> T
): T {
  return resource(namespace, name, fileExtension, prefix, classLoader).openStream().use { parser.invoke(it) }
}

@Throws(IllegalStateException::class)
fun rootResource(classLoader: ClassLoader = AvroKotlin.Constants.DEFAULT_CLASS_LOADER): URL = classLoader.getResource("")
  ?: throw IllegalStateException("no root resource found for classLoader:$classLoader")

fun findAvroResources(prefix: String? = null, classLoader: ClassLoader = AvroKotlin.Constants.DEFAULT_CLASS_LOADER) {
  val rootPath = Path(rootResource(classLoader).path).resolve(prefix ?: "")

  Files.walk(rootPath)
    .map { it.toFile() }
    .forEach { println(it) }
}

