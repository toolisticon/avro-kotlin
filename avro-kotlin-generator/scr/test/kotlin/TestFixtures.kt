package io.toolisticon.kotlin.avro.generator


import io.toolisticon.kotlin.avro.AvroParser
import mu.KLogging

object TestFixtures : KLogging() {
  val parser = AvroParser()

  //fun contextFactory() = AvroKotlinGeneratorContextFactory()


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
