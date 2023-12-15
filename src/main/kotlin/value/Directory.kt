package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.value.AvroSpecification.SCHEMA
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.streams.asSequence

@JvmInline
value class Directory(override val value: Path) : ValueType<Path> {
  companion object {
    private fun Sequence<Path>.filterSpecification(specification: AvroSpecification) = this.filter {
      it.isRegularFile() && specification.value.equals(it.extension)
    }
  }

  init {
    require(value.isDirectory() && value.exists()) { "Value has to be an existing directory." }
  }

  constructor(value: File) : this(value = value.toPath())

  /**
   * Takes a directory and extends it with a given path to get a file path.
   */
  fun resolve(subPath: Path): Path = value.resolve(subPath)

  fun walk(): Sequence<Path> = Files.walk(value).asSequence()

  fun findSchemaFiles() = walk().filterSpecification(SCHEMA)

  /**
   * Write content to given file, creates directory path if it not exists.
   */
  fun write(fqn: CanonicalName, type: AvroSpecification, content: JsonString): Path {
    val fqnPath = resolve(fqn.toPath(type))
    fqnPath.parent.createDirectories()

    fqnPath.writeText(content.value, AvroKotlin.UTF_8)

    return fqnPath
  }

  override fun toString() = "Directory($value)"
}
