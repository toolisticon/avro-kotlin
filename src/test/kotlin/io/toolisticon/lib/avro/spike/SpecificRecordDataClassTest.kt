package io.toolisticon.lib.avro.spike

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.toolisticon.lib.avro.compiler.SpecificKotlinCompilerFactory
import mu.KLogging
import org.apache.avro.Schema
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.streams.toList


internal class SpecificRecordDataClassTest {
  companion object : KLogging() {

    const val EVENT_SCHEMA = """
      {
  "namespace": "spike.event",
  "name": "BankAccountCreated",
  "doc": "A new Bank Account has been created.",
  "type": "record",
  "fields": [
    {
      "name": "bankAccountId",
      "type": {
        "type": "string"
      }
    },
    {
      "name": "initialBalance",
      "type": {
        "type": "int"
      }
    }
  ]
}

    """

//    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    final String utf8 = StandardCharsets.UTF_8.name();
//    try (PrintStream ps = new PrintStream(baos, true, utf8)) {
//      yourFunction(object, ps);
//    }
//    String data = baos.toString(utf8);
  }

  @TempDir
  lateinit var anotherTempDir: File

  @Test
  fun `use avro-compiler for kotlin`() {
    val basePath = "/Users/jangalinski/IdeaProjects/toolisticon/avro-kotlin"

    val compiler = SpecificCompiler(Schema.Parser().parse(EVENT_SCHEMA)).apply {
      setTemplateDir(SpecificKotlinCompilerFactory.TEMPLATE_DIR)
      setStringType(GenericData.StringType.String)
      setSuffix(".kt")
      setAdditionalVelocityTools(listOf(SpecificKotlinCompilerFactory()))
    }.compileToDestination(null, anotherTempDir)

    val generated = Files.walk(anotherTempDir.toPath()).filter { it.isRegularFile() }
      .map { it.name to it.readText() }
      .toList().single()

    logger.info { """
      ${generated.first}
      -----
      ${generated.second}
      -----
    """.trimIndent() }



    val result = KotlinCompilation().apply {
      sources = listOf(SourceFile.kotlin(name = generated.first, contents = generated.second, trimIndent = true))

      // pass your own instance of an annotation processor
      //annotationProcessors = listOf(MyAnnotationProcessor())

      // pass your own instance of a compiler plugin
      //compilerPlugins = listOf(MyComponentRegistrar())
      //commandLineProcessors = listOf(MyCommandlineProcessor())

      inheritClassPath = true
      //messageOutputStream = System.out // see diagnostics in real time
    }.compile()

    logger.error { result.messages }
      assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

    // Test diagnostic output of compiler
//    assertThat(result.messages).contains("My annotation processor was called")

    // Load compiled classes and inspect generated code through reflection
    val kClazz = result.classLoader.loadClass("spike.event.BankAccountCreated")
    assertThat(kClazz).hasDeclaredMethods("foo")

  }
}
