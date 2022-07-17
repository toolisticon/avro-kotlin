package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.AvroKotlinLib.EXTENSION_PROTOCOL
import io.toolisticon.lib.avro.FileExtension
import io.toolisticon.lib.avro.ext.IoExt.file
import io.toolisticon.lib.avro.ext.IoExt.writeText
import io.toolisticon.lib.avro.fqn.ProtocolFqn
import org.apache.avro.Protocol
import java.io.File
import java.nio.file.Path

object ProtocolExt {

  /**
   * Protocol file extension is `avpr`.
   */
  val Protocol.fileExtension: FileExtension get() = EXTENSION_PROTOCOL

  /**
   * Protocol file path based on namespace, name and extension.
   */
  val Protocol.path: Path get() = IoExt.fqnToPath(namespace, name, fileExtension)

  /**
   * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
   */
  fun Protocol.file(dir: File): Path = dir.file(this.path)

  fun Protocol.fqn(): ProtocolFqn = ProtocolFqn(namespace = namespace, name = name)

  /**
   * Write protocol json content to directory, using [Protocol#path].
   */
  fun Protocol.writeToDirectory(dir: File): Path {
    val target: File = file(dir).toFile()
    target.writeText(toString(true))

    return target.toPath()
  }

  /**
   * Get message from protocol by name.
   */
  fun Protocol.message(name:String): Protocol.Message = requireNotNull(this.messages[name]) {"No protocol message with name '$name' found."}

}
