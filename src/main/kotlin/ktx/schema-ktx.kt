@file: JvmName("SchemaKtx")

package io.toolisticon.avro.kotlin.ktx

import io.toolisticon.avro.kotlin.value.Directory
import io.toolisticon.avro.kotlin._bak.SchemaFqn
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificData
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KClass


val Schema.path: Path get() = fqnToPath(Namespace(namespace), Name(name), AvroSpecification.SCHEMA.value)

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

fun schemaForClass(recordClass: Class<*>): Schema = SpecificData(recordClass.classLoader).getSchema(recordClass)

fun schemaForClass(recordClass: KClass<*>): Schema = schemaForClass(recordClass.java)


