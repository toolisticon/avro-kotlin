package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.Directory
import io.toolisticon.avro.kotlin.FileExtension
import io.toolisticon.avro.kotlin.Fingerprint
import io.toolisticon.avro.kotlin.ktx.file
import io.toolisticon.avro.kotlin.ktx.fqnToPath
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificData
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KClass

@Deprecated("remove")
object SchemaExt {


  /**
   * The schema fingerprint as defined by [SchemaNormalization.parsingFingerprint64].
   */
  val Schema.fingerprint: Fingerprint get() = SchemaNormalization.parsingFingerprint64(this)

  val Schema.fileExtension: FileExtension get() = AvroKotlin.Constants.EXTENSION_SCHEMA

  val Schema.path: Path get() = fqnToPath(Namespace(namespace), Name(name), fileExtension)

  /**
   * Get [Schema] as [ByteArrayInputStream].
   */
  fun Schema.byteContent(): ByteArrayInputStream = this.toString().byteInputStream(AvroKotlin.Constants.UTF_8)

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

  fun Schema.fqn() = SchemaFqn(namespace = Namespace(namespace), name = Name(name))


  fun Schema.writeToDirectory(dir: Directory, path: Path = this.path): File {
    val content = toString(true)
    val file = dir.file(path).toFile()

    return file.apply { writeText(toString(true)) }
  }

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>): Schema = SpecificData(recordClass.classLoader).getSchema(recordClass)

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>): Schema = schemaForClass(recordClass.java)

}
