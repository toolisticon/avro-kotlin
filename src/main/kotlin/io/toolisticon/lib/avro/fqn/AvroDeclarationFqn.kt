package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.FileExtension
import io.toolisticon.lib.avro.Name
import io.toolisticon.lib.avro.Namespace
import io.toolisticon.lib.avro.ext.IoExt.NAME_SEPARATOR
import org.apache.avro.AvroRuntimeException
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.jvm.Throws

/**
 * [AvroRuntimeException] expressing the the declared canonical name in the actual resource does not match the path of the resource.
 */
class AvroDeclarationMismatchException(actual: AvroDeclarationFqn, expected: AvroDeclarationFqn)
  : AvroRuntimeException("violation of package-path convention: found declaration fqn='${actual.canonicalName}' but was loaded from path='${expected.path}'")


/**
 * Represents a concrete file or resource containing a (json) avro schema or protocol.
 */
sealed interface AvroDeclarationFqn : AvroFqn {
  /**
   * The file suffix to use (avsc or avpr).
   */
  val fileExtension: FileExtension

  /**
   * Calculates the FQN path of the document using namespace, name and suffix
   */
  val path: Path
}

//fun Path.toFqn(): AvroDeclarationFqn {
//  val namespace = this.parent.toString().replace(File.separator, NAME_SEPARATOR)
//  val fileExtension = this.extension
//  val name = fileName.toString().substringBeforeLast(NAME_SEPARATOR)
//
//
//  return AvroDeclarationFqnData(namespace, name, fileExtension)
//}

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
