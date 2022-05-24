package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.*
import io.toolisticon.lib.avro.AvroKotlinLib.NAME_SEPARATOR
import io.toolisticon.lib.avro.AvroKotlinLib.canonicalName
import io.toolisticon.lib.avro.io.canonicalNameToPath
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.extension

/**
 * Represents a concrete file or resource containing a (json) avro schema or protocol.
 */
interface AvroDeclarationFqn : AvroFqn {

  /**
   * The file suffix to use (avsc or avpr).
   */
  val fileExtension: FileExtension

  /**
   * Calculates the FQN path of the document using namespace, name and suffix
   */
  val path: Path
}

/**
 * Abstract implementation of [AvroDeclarationFqn] providing default for canonicalName and path.
 */
abstract class AbstractAvroDeclarationFqn(override val namespace: Namespace, override val name: Name, override val fileExtension: FileExtension) :
  AvroDeclarationFqn {

  override val canonicalName: CanonicalName by lazy {
    canonicalName(namespace, name)
  }

  override val path: Path by lazy {
    canonicalNameToPath(canonicalName, fileExtension)
  }

  override fun toString() = "${this::class.simpleName}(namespace='$namespace', name='$name', extension='$fileExtension')"
}

data class AvroDeclarationFqnData(
  override val namespace: Namespace,
  override val name: Name,
  override val fileExtension: FileExtension
) : AbstractAvroDeclarationFqn(namespace, name, fileExtension) {

  constructor(fqn: SchemaFqn) : this(fqn.namespace, fqn.name, fqn.fileExtension)
  constructor(fqn: ProtocolFqn) : this(fqn.namespace, fqn.name, fqn.fileExtension)

}

fun Path.toFqn(): AvroDeclarationFqn {
  val namespace = this.parent.toString().replace(File.separator, NAME_SEPARATOR)
  val fileExtension = this.extension
  val name = fileName.toString().substringBeforeLast(NAME_SEPARATOR)


  return AvroDeclarationFqnData(namespace, name, fileExtension)
}

/**
 * URL of resource using path based on namespace and name.
 * @param prefix optional prefix if resource is not under root in classpath
 * @param classLoader optional classloader if not given, the classloader of AvroFqn is used
 * @return URL pointing to resource.
 */
fun AvroDeclarationFqn.resource(prefix: String? = null, classLoader: ClassLoader = AvroKotlinLib::class.java.classLoader): URL {
  val resource = (if (prefix != null) {
    "$prefix${File.separator}${path}"
  } else {
    path.toString()
  }).removePrefix(File.separator)
  return requireNotNull(classLoader.getResource(resource)) { "resource not found: ${path}" }
}
