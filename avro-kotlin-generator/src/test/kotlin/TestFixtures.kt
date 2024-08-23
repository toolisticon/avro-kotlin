package io.toolisticon.kotlin.avro.generator


import _ktx.ResourceKtx.resourceUrl
import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.AvroParser
import mu.KLogging
import java.time.Instant

object TestFixtures : KLogging() {
  val NOW = Instant.parse("2024-08-21T23:19:02.152209Z")
  val NOW_SUPPLER = { NOW }
  val PARSER = AvroParser()

  val DEFAULT_PROPERTIES = AvroKotlinGeneratorProperties(nowSupplier = NOW_SUPPLER)
  val DEFAULT_GENERATOR = AvroKotlinGenerator(properties = DEFAULT_PROPERTIES)

  fun parseDeclaration(path: String) = PARSER.parseSchema(resourceUrl(path))

  fun expectedSource(className: ClassName) = resourceUrl("generated/$className.txt").readText()

  fun <K : Any, V : Any> Map<K, V>.toReadableString() = StringBuilder()
    .apply {
      this@toReadableString.forEach { (k, v) ->
        appendLine("$k:")
        appendLine("\t$v")
        appendLine()
      }
    }
    .toString()

}


annotation class MyAnnotation
