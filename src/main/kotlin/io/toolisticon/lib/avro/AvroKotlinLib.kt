package io.toolisticon.lib.avro

import io.toolisticon.lib.avro.declaration.SchemaDeclaration
import io.toolisticon.lib.avro.fqn.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

object AvroKotlinLib {

  var verifyPackageConvention = true

  fun schema(namespace: Namespace, name: Name): SchemaFqn = SchemaFqn(namespace = namespace, name = name)
  fun schema(fqn: AvroFqn): SchemaFqn = schema(namespace = fqn.namespace, name = fqn.name)

  fun protocol(namespace: Namespace, name: Name): ProtocolFqn = ProtocolFqn(namespace = namespace, name = name)
  fun protocol(fqn: AvroFqn): ProtocolFqn = protocol(namespace = fqn.namespace, name = fqn.name)


  /**
   * Create [AvroFqn] based on namespace and name.
   */
  fun fqn(namespace: Namespace, name: Name): AvroFqn = AvroFqnData(namespace = namespace, name = name)

  fun findDeclarations(root: Path, filter: (Path) -> Boolean = Declaration.BOTH): List<Any> {
    val fqns = Files.walk(root)
      .filter { it.isRegularFile() }
      .filter(filter)
      .map { GenericAvroDeclarationFqn.fromPath(it, root) }
      .toList()

    return fqns.map { fqn ->
      if (fqn.fileExtension == Declaration.SCHEMA.fileExtension) {
        SchemaDeclaration(
          location = fqn,
          content = schema(fqn).fromDirectory(root.toFile())
        )
      } else if (fqn.fileExtension == Declaration.PROTOCOL.fileExtension) {
        protocol(fqn).fromDirectory(root.toFile())
      } else {
        throw IllegalArgumentException("unsupported extension: ${fqn.fileExtension}")
      }
    }
  }

  /**
   * An avro declaration file should have a path that matches its canonicalName, the same way a java file has to
   * be defined in the correct package.
   * @throws AvroDeclarationMismatchException
   */
  @Throws(AvroDeclarationMismatchException::class)
  fun verifyPackagePathConvention(actual: GenericAvroDeclarationFqn, expected: GenericAvroDeclarationFqn): Unit = try {
    require(actual == expected)
  } catch (e: Exception) {
    throw AvroDeclarationMismatchException(actual, expected)
  }

  enum class Declaration(val fileExtension: FileExtension) {
    SCHEMA(EXTENSION_SCHEMA),
    PROTOCOL(EXTENSION_PROTOCOL)
    ;

    companion object {
      private val extensions = values().associateBy { it.fileExtension }

      val BOTH: (Path) -> Boolean = { setOf(SCHEMA.fileExtension, PROTOCOL.fileExtension).contains(it.extension) }

      fun byExtension(fileExtension: FileExtension) = requireNotNull(extensions[fileExtension]) { "illegal file extension='$fileExtension'" }
    }
  }


  /**
   * Marker bytes according to Avro schema specification v1.
   */
  @JvmField
  val AVRO_V1_HEADER = byteArrayOf(-61, 1) // [C3 01]

  /**
   * Length of default avro header bytes.
   */
  val AVRO_HEADER_LENGTH = AVRO_V1_HEADER.size + Long.SIZE_BYTES

  /**
   * Avro Schema files end with `.avsc`.
   */
  const val EXTENSION_SCHEMA: FileExtension = "avsc"

  /**
   * Avro protocol files end with `.avpr`.
   */
  const val EXTENSION_PROTOCOL: FileExtension = "avpr"

  /**
   * Default charset to use is [Charsets#UTF_8]
   */
  val UTF_8 = Charsets.UTF_8

  /**
   * Default [ClassLoader] for resource loading.
   */
  val DEFAULT_CLASS_LOADER: ClassLoader = AvroKotlinLib::class.java.classLoader

}


/**
 * An avro namespace.
 */
typealias Namespace = String

/**
 * An avro name.
 */
typealias Name = String

/**
 * Combining namespace and name as a [java.lang.Class#canonicalName].
 */
typealias CanonicalName = String

/**
 * A file extension.
 */
typealias FileExtension = String

/**
 * The Schema fingerprint.
 */
typealias Fingerprint = Long

/**
 * Message encoded as [Single Object](https://avro.apache.org/docs/current/spec.html#single_object_encoding) ByteArray.
 */
typealias SingleObjectEncoded = ByteArray

/**
 * The encoded message. This is only the payload data,
 * so no marker header and encoded schemaId are present.
 */
typealias SingleObjectPayload = ByteArray

typealias Directory = File
