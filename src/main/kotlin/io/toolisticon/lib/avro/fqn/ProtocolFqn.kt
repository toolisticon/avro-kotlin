package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.AvroKotlinLib.DEFAULT_CLASS_LOADER
import io.toolisticon.lib.avro.Name
import io.toolisticon.lib.avro.Namespace
import io.toolisticon.lib.avro.ext.IoExt.file
import io.toolisticon.lib.avro.ext.ResourceExt.parseFromResource
import org.apache.avro.Protocol
import java.io.File

/**
 * Represents a (json) avro [Protocol] file. Based on this information the file can be read from resource and read/written from/to a file.
 */
data class ProtocolFqn(override val namespace: Namespace, override val name: Name) : AbstractAvroDeclarationFqn(
  namespace = namespace, name = name, fileExtension = AvroKotlinLib.EXTENSION_PROTOCOL
) {
  companion object {
    /**
     * Converts [AvroFqn] to [ProtocolFqn].
     */
    fun AvroFqn.protocolFqn() = AvroKotlinLib.protocol(this)
  }

  override fun toString(): String = super.toString()
}

/**
 * Load from resource.
 *
 * @param prefix optional path inside the classpath if not in root
 * @param classLoader optional - if specific one is required, otherwise classloader of [AvroKotlinLib] is used
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
