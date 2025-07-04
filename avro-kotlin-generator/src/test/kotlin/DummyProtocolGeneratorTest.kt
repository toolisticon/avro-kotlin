package io.toolisticon.kotlin.avro.generator

import _ktx.ResourceKtx.resourceUrl
import io.github.oshai.kotlinlogging.KotlinLogging
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.tag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DummyProtocolGeneratorTest {
  private val logger = KotlinLogging.logger {}

  private val generator = TestFixtures.DEFAULT_GENERATOR
  private val declaration: ProtocolDeclaration = TestFixtures.PARSER.parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

  @Test
  fun `generate dummy protocol`() {
    val files = generator.generate(declaration)

    files.map(KotlinFileSpec::code).forEach { logger.info { "${it}\n" } }
  }

  @Test
  fun `tag context`() {
    data class DummyTag(val value: String)

    val context = ProtocolDeclarationContext.of(
      declaration,
      registry = TestFixtures.DEFAULT_REGISTRY,
      properties = TestFixtures.DEFAULT_PROPERTIES
    )
    assertThat(context.tag<DummyTag>()).isNull()
    context.tags.put(DummyTag::class, DummyTag("test"))

    assertThat(context.tag<DummyTag>()).isNotNull
    assertThat(context.tag<DummyTag>()?.value).isEqualTo("test")
  }
}
