package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.Directory
import io.toolisticon.lib.avro.FileExtension
import io.toolisticon.lib.avro.Fingerprint
import io.toolisticon.lib.avro.ext.IoExt.file
import io.toolisticon.lib.avro.ext.IoExt.fqnToPath
import io.toolisticon.lib.avro.ext.IoExt.writeText
import io.toolisticon.lib.avro.fqn.SchemaFqn
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.apache.avro.generic.GenericData
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Path

object SchemaExt {


  /**
   * The schema fingerprint as defined by [SchemaNormalization.parsingFingerprint64].
   */
  val Schema.fingerprint: Fingerprint get() = SchemaNormalization.parsingFingerprint64(this)

  val Schema.fileExtension: FileExtension get() = AvroKotlinLib.EXTENSION_SCHEMA

  val Schema.path: Path get() = fqnToPath(namespace, name, fileExtension)

  /**
   * Get [Schema] as [ByteArrayInputStream].
   */
  fun Schema.byteContent(): ByteArrayInputStream = this.toString().byteInputStream(AvroKotlinLib.UTF_8)

  /**
   * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
   */
  fun Schema.file(dir: File): Path = dir.file(this.path)

  /**
   * Write schema json content to directory, using [Schema#path].
   */
  fun Schema.writeToDirectory(dir: File): Path {
    val target: File = file(dir).toFile()
    target.writeText(toString(true))

    return target.toPath()
  }

  /**
   * Creates a schema compliant generic record.
   *
   * @param receiver - a lambda that can modify the record (basically: use `put` to fill in data
   * @return a schema compliant record instance
   */
  fun Schema.createGenericRecord(receiver: GenericData.Record.() -> Unit) = GenericData.Record(this).apply { receiver.invoke(this) }

  fun Schema.fqn() = SchemaFqn(namespace = namespace, name = name)


  fun Schema.writeToDirectory(dir: Directory, path: Path = this.path): File {
    val content = toString(true)
    val file = dir.file(path).toFile()

    return file.writeText(toString(true))
  }

}
