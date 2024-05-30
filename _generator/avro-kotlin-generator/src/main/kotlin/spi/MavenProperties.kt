package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.AnnotationSpec
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import mu.KLogging
import java.time.Instant
import java.util.*
import jakarta.annotation.Generated


object MavenProperties : KLogging() {
  private val properties = Properties().apply {
    try {
      load(MavenProperties::class.java.getResourceAsStream("/avro-kotlin-generator-maven.properties"))
    } catch (e: Exception) {
      logger.warn { "could not load resource: 'avro-kotlin-generator-maven.properties'" }
    }
  }

  internal var nowSupplier: () -> Instant = Instant::now

  data class Versions(
    val avro: String,
    val avro4k: String,
    val avro4kGenerator: String,
    val kotlinPoet: String,
  )

  val versions: Versions

  init {
    with(properties) {
      versions = Versions(
        avro = getProperty("avro.version", "n/a"),
        avro4k = getProperty("avro4k.version", "n/a"),
        avro4kGenerator = getProperty("avro-kotlin-generator.version", "n/a"),
        kotlinPoet = getProperty("kotlin-poet.version", "n/a"),
      )
    }
  }

  val generatedAnnotation: AnnotationSpec = AnnotationSpec.builder(Generated::class)
    .addMember(
      // FIXME: value has to be array
      "value=%S, date=%S, comments=%S",
      AvroKotlinGenerator::class.qualifiedName!!,
      nowSupplier(),
      versions
    )
    .build()

  override fun toString(): String {
    return properties.toString()
  }
}
