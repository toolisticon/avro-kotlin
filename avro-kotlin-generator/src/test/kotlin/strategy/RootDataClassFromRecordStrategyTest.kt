package io.toolisticon.kotlin.avro.generator.strategy

import _ktx.ResourceKtx.resourceUrl
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.TestFixtures.DEFAULT_PROPERTIES
import io.toolisticon.kotlin.avro.generator.TestFixtures.PARSER
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotlinPoetApi::class)
internal class RootDataClassFromRecordStrategyTest {

  private val registry = AvroCodeGenerationSpiRegistry(registry = AvroCodeGenerationSpiRegistry.load())
  private val strategy = RootDataClassStrategy()

  @Test
  fun `generate simple root data class`() {
    val url = resourceUrl("schema/SimpleStringRecord.avsc")
    val declaration = PARSER.parseSchema(url)
    assertThat(declaration.schema.isRoot).isTrue()

    val context = SchemaDeclarationContext.of(declaration, registry)
      .copy(properties = DEFAULT_PROPERTIES)

    val spec = requireNotNull(strategy.execute(context, declaration.recordType))

    val expectedContent = resourceUrl("generated/${spec.className}.txt").readText()

    assertThat(buildFile(spec.className) { addType(spec) }.code).isEqualToIgnoringWhitespace(expectedContent)
  }
}
