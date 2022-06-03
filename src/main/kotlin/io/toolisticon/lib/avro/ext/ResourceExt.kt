package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.FileExtension
import io.toolisticon.lib.avro.Name
import io.toolisticon.lib.avro.Namespace
import io.toolisticon.lib.avro.fqn.DefaultAvroDeclarationFqn
import org.apache.avro.JsonProperties
import java.io.File
import java.io.InputStream
import java.net.URL

object ResourceExt {


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
    fileExtension: FileExtension,
    prefix: String? = null,
    classLoader: ClassLoader = AvroKotlinLib.DEFAULT_CLASS_LOADER
  ): URL {
    val fqn = DefaultAvroDeclarationFqn(namespace, name, fileExtension)
    val path = fqn.path

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

  @kotlin.jvm.Throws(IllegalStateException::class)
  fun rootResource(classLoader: ClassLoader = AvroKotlinLib.DEFAULT_CLASS_LOADER): URL = classLoader.getResource("")
    ?: throw IllegalStateException("no root resource found for classLoader:$classLoader")

}
