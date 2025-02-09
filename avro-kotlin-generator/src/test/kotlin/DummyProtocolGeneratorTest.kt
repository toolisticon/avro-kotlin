package io.toolisticon.kotlin.avro.generator

import _ktx.ResourceKtx.resourceUrl
import io.github.oshai.kotlinlogging.KotlinLogging
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import org.junit.jupiter.api.Test

class DummyProtocolGeneratorTest {
  private val logger = KotlinLogging.logger {}

  private val generator = TestFixtures.DEFAULT_GENERATOR
  private val declaration: ProtocolDeclaration = TestFixtures.PARSER.parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

  @Test
  fun `generate dummy protocol`() {
    println(declaration.originalJson)

    val files = generator.generate(declaration)

    files.map(KotlinFileSpec::code).forEach { logger.info { "${it}\n" } }
  }
}
