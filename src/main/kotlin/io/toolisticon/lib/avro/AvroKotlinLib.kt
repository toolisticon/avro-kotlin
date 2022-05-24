package io.toolisticon.lib.avro

import io.toolisticon.lib.avro.ext.IoExt.NAME_SEPARATOR
import io.toolisticon.lib.avro.fqn.AvroFqn
import io.toolisticon.lib.avro.fqn.AvroFqnData
import io.toolisticon.lib.avro.fqn.ProtocolFqn
import io.toolisticon.lib.avro.fqn.SchemaFqn
import java.io.File

object AvroKotlinLib {

  fun schema(namespace: Namespace, name: Name): SchemaFqn = SchemaFqn(namespace = namespace, name = name)
  fun schema(fqn: AvroFqn): SchemaFqn = schema(namespace = fqn.namespace, name = fqn.name)

  fun protocol(namespace: Namespace, name: Name): ProtocolFqn = ProtocolFqn(namespace = namespace, name = name)
  fun protocol(fqn: AvroFqn): ProtocolFqn = protocol(namespace = fqn.namespace, name = fqn.name)


  /**
   * Concat a canonical name based on namespace and name.
   */
  fun canonicalName(namespace: Namespace, name: Name): CanonicalName = "$namespace$NAME_SEPARATOR$name"

  /**
   * Create [AvroFqn] based on namespace and name.
   */
  fun fqn(namespace: Namespace, name: Name): AvroFqn = AvroFqnData(namespace = namespace, name = name)


//  @Throws(AvroSchemaFqnMismatch::class)
//  fun verifyPathAndSchemaFqnMatches(rootDirectory: File, avscFile: File, parser: Schema.Parser = Schema.Parser()): Schema {
//    val subPath: Path = Path(avscFile.path.removePrefix("${rootDirectory.path}${File.separator}"))
//
//    val fileFqn: AvroFqn = fileToFqn(subPath)
//
//    if (fileFqn.namespace != schema.namespace || fileFqn.name != schema.name) {
//      throw AvroSchemaFqnMismatch(schema.namespace, schema.name, subPath)
//    }
//
//    return schema
//  }

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
