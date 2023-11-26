@file: JvmName("ProtocolKtx")

package io.toolisticon.avro.kotlin.ktx

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.documentation
import io.toolisticon.avro.kotlin._bak.ProtocolFqn
import io.toolisticon.avro.kotlin.model.AvroSchema
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.File
import java.nio.file.Path

val Protocol.fingerprint: AvroFingerprint get() = AvroFingerprint(this)
val Protocol.hashCode: AvroHashCode get() = AvroHashCode(this)

val Protocol.documentation: Documentation? get() = documentation(this.doc)
val Protocol.Message.documentation: Documentation? get() = documentation(this.doc)

val Protocol.json: String get() = this.toString(true)

val Protocol.Message.requestName: Name get() = Name("${this.name.firstUppercase()}Request")

fun Protocol.Message.createRecord(namespace: Namespace): AvroSchema = AvroSchema(
  Schema.createRecord(
    this.requestName.value,
    doc,
    namespace.value,
    this.request.isError,
    this.request.fields.map { Schema.Field(it, it.schema()) })
)

// TODO: hopefully we will not need this reflection stuff, but if the above createRecord method loses the reference, we might.
private val schemaFieldPosition = Schema.Field::class.java.getDeclaredField("position").apply { isAccessible = true }
fun Schema.Field.resetPosition() = schemaFieldPosition.set(this, -1)
fun List<Schema.Field>.resetPositions() = forEach(Schema.Field::resetPosition)

/**
 * Protocol file extension is `avpr`.
 */
val Protocol.fileExtension: String get() = AvroKotlin.Constants.EXTENSION_PROTOCOL

/**
 * Protocol file path based on namespace, name and extension.
 */
val Protocol.path: Path get() = fqnToPath(Namespace(namespace), Name(name), fileExtension)

/**
 * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
 */
fun Protocol.file(dir: File): Path = dir.file(this.path)

fun Protocol.fqn(): ProtocolFqn = ProtocolFqn(namespace = Namespace(namespace), name = Name(name))

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
fun Protocol.message(name: String): Protocol.Message = requireNotNull(this.messages[name]) { "No protocol message with name '$name' found." }
