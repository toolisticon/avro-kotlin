package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.FileExtension
import io.toolisticon.lib.avro.Name
import io.toolisticon.lib.avro.Namespace
import io.toolisticon.lib.avro.ext.IoExt
import java.nio.file.Path

/**
 * Generic, default, implementation of [AvroDeclarationFqn].
 *
 * This can not be a data class, because it is overwritten by the concrete [SchemaFqn] and [ProtocolFqn] data classes.
 */
open class DefaultAvroDeclarationFqn(private val fqn: AvroFqn, override val fileExtension: FileExtension) : AvroDeclarationFqn, AvroFqn by fqn {

  constructor(namespace: Namespace, name: Name, fileExtension: FileExtension) : this(AvroFqnData(namespace, name), fileExtension)

  constructor(fqn: SchemaFqn) : this(fqn.namespace, fqn.name, fqn.fileExtension)
  constructor(fqn: ProtocolFqn) : this(fqn.namespace, fqn.name, fqn.fileExtension)


  override val path: Path by lazy {
    IoExt.canonicalNameToPath(canonicalName, fileExtension)
  }


  override fun toString() = "${this::class.simpleName}(namespace='$namespace', name='$name', fileExtension='$fileExtension')"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DefaultAvroDeclarationFqn) return false

    if (fqn != other.fqn) return false
    if (fileExtension != other.fileExtension) return false

    return true
  }

  override fun hashCode(): Int {
    var result = fqn.hashCode()
    result = 31 * result + fileExtension.hashCode()
    return result
  }
}
