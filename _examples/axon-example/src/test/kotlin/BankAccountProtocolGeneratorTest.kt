package io.holixon.axon.avro.generator

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.holixon.axon.avro.generator.meta.FieldMetaData.Companion.fieldMetaData
import io.holixon.axon.avro.generator.meta.RecordMetaData.Companion.recordMetaData
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.generation.test.KotlinCodeGenerationTest
import io.toolisticon.kotlin.generation.test.model.KotlinCompilationCommand
import mu.KLogging
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotlinPoetApi::class, ExperimentalCompilerApi::class)
class BankAccountProtocolGeneratorTest {
  companion object : KLogging()


  @Test
  fun `generate protocol`() {

    val files = TestFixtures.DEFAULT_GENERATOR.generate(declaration)
    files.forEach{
      // logger.info { "===== FILE: ${it.fqn}\n${it.code}\n" }
    }

    println("Compiling....")
    val result = KotlinCodeGenerationTest.compile(
      KotlinCompilationCommand(
        fileSpecs = files
      )
    )
    println("Compilation ready.")


    result.shouldBeOk()
    result.result.messages.lines()
      .filter { line -> line.endsWith(".kt") }
      .distinct()
      .forEach { message -> println("file://$message") }
  }

  private val declaration = TestFixtures.parseProtocol("BankAccountProtocol.avpr")
}
