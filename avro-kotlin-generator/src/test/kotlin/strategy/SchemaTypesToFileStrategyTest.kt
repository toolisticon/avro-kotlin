package io.toolisticon.kotlin.avro.generator.strategy

import _ktx.ResourceKtx.resourceUrl
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.TestFixtures.DEFAULT_PROPERTIES
import io.toolisticon.kotlin.avro.generator.TestFixtures.PARSER
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotlinPoetApi::class)
internal class SchemaTypesToFileStrategyTest {

  private val registry = AvroCodeGenerationSpiRegistry(registry = AvroCodeGenerationSpiRegistry.load())
  private val strategy = SchemaTypesToFileStrategy()

  @Test
  fun `generate simple root data class`() {
    val url = resourceUrl("schema/SimpleStringRecord.avsc")
    val declaration = PARSER.parseSchema(url)
    assertThat(declaration.schema.isRoot).isTrue()

    val context = SchemaDeclarationContext.of(declaration, registry)
      .copy(properties = DEFAULT_PROPERTIES)

    val file = requireNotNull(strategy.execute(context, declaration))

    val expectedContent = resourceUrl("generated/${file.className}.txt").readText()

    assertThat(file.code).isEqualToIgnoringWhitespace(expectedContent)
  }
}
