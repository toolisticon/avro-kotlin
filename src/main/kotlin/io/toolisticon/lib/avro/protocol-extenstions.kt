package io.toolisticon.lib.avro

import io.toolisticon.lib.avro.io.file
import io.toolisticon.lib.avro.io.fqnToPath
import io.toolisticon.lib.avro.io.writeText
import org.apache.avro.Protocol
import java.io.File
import java.nio.file.Path

/**
 * Protocl file extension is `avpr`.
 */
val Protocol.fileExtension: FileExtension get() = AvroKotlinLib.EXTENSION_PROTOCOL

/**
 * Protocol file path based on namespace, name and extension.
 */
val Protocol.path: Path get() = fqnToPath(namespace, name, fileExtension)

/**
 * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
 */
fun Protocol.file(dir: File): Path = dir.file(this.path)

/**
 * Write protocol json content to directory, using [Protocol#path].
 */
fun Protocol.writeToDirectory(dir: File): Path {
  val target: File = file(dir).toFile()
  target.writeText(toString(true))

  return target.toPath()
}
