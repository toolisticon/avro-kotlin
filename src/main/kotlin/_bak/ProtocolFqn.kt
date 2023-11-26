package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.DEFAULT_CLASS_LOADER
import io.toolisticon.avro.kotlin.ktx.file
import io.toolisticon.avro.kotlin.ktx.parseFromResource
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.Protocol
import java.io.File

/**
 * Represents a (json) avro [Protocol] file. Based on this information the file can be read from resource and read/written from/to a file.
 */
@Deprecated("remove")
data class ProtocolFqn(override val namespace: Namespace, override val name: Name) : GenericAvroDeclarationFqn(
  namespace = namespace, name = name, fileExtension = AvroKotlin.Constants.EXTENSION_PROTOCOL
) {
  companion object {
    /**
     * Converts [AvroFqn] to [ProtocolFqn].
     */
    fun AvroFqn.protocolFqn() = AvroKotlin.protocol(this)
  }

  override fun toString(): String = super.toString()
}

/**
 * Load from resource.
 *
 * @param prefix optional path inside the classpath if not in root
 * @param classLoader optional - if specific one is required, otherwise classloader of [AvroKotlin] is used
 * @return parsed avro  protocol instance
 */
fun ProtocolFqn.fromResource(
  prefix: String? = null,
  classLoader: ClassLoader = DEFAULT_CLASS_LOADER
): Protocol = parseFromResource(
  namespace = namespace,
  name = name,
  fileExtension = this.fileExtension,
  classLoader = classLoader,
  prefix = prefix
) { Protocol.parse(it) }

/**
 * Read [Protocol] from directory assuming file has canonical path.
 */
fun ProtocolFqn.fromDirectory(dir: File): Protocol = Protocol.parse(dir.file(path).toFile())
