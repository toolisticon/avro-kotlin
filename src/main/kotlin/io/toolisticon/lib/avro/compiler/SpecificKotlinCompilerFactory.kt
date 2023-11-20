package io.toolisticon.lib.avro.compiler

import org.apache.avro.Schema
import org.apache.avro.compiler.specific.SpecificCompiler

class SpecificKotlinCompilerFactory {
  companion object {
    const val TEMPLATE_DIR = "/org/apache/avro/compiler/specific/templates/kotlin/classic/"
  }
  fun create(compiler: SpecificCompiler, schema: Schema): SpecificKotlinCompiler = SpecificKotlinCompiler(compiler)
}
