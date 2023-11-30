package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.FileExtension
import io.toolisticon.avro.kotlin.ktx.canonicalNameToPath
import io.toolisticon.avro.kotlin.ktx.dashToDot
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.relativeToOrSelf

/**
 * Generic, default, implementation of [AvroDeclarationFqn].
 *
 * This can not be a data class, because it is overwritten by the concrete [SchemaFqn] and [ProtocolFqn] data classes.
 */
@Deprecated("remove")
open class GenericAvroDeclarationFqn(
  private val fqn: AvroFqn,
  override val fileExtension: FileExtension
) : AvroDeclarationFqn, AvroFqn by fqn {
  companion object {
    fun fromPath(path: Path, root: Path? = null): GenericAvroDeclarationFqn {
      val fqnPath = path.relativeToOrSelf(root ?: Path(""))

      return GenericAvroDeclarationFqn(
        Namespace(fqnPath.parent.toString().dashToDot()),
        Name(fqnPath.nameWithoutExtension),
        fqnPath.extension
      )
    }
  }

  constructor(namespace: Namespace, name: Name, fileExtension: FileExtension) : this(AvroFqnData(namespace, name), fileExtension)

  constructor(fqn: SchemaFqn) : this(fqn.namespace, fqn.name, fqn.fileExtension)
  constructor(fqn: ProtocolFqn) : this(fqn.namespace, fqn.name, fqn.fileExtension)


  override val path: Path by lazy {
    canonicalNameToPath(canonicalName, fileExtension)
  }

  override val type: AvroKotlin.Declaration by lazy {
    AvroKotlin.Declaration.byExtension(fileExtension)
  }

  override fun toString() = "${this::class.simpleName}(namespace='$namespace', name='$name', fileExtension='$fileExtension')"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is GenericAvroDeclarationFqn) return false

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
