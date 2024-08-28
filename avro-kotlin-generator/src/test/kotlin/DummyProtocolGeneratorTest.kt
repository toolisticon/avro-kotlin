package io.toolisticon.kotlin.avro.generator

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import org.junit.jupiter.api.Test

class DummyProtocolGeneratorTest {

  private val generator = TestFixtures.DEFAULT_GENERATOR
  private val declaration : ProtocolDeclaration = TestFixtures.PARSER.parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

  @Test
  fun `generate dummy protocol`() {
    println(declaration.originalJson)

    val file = generator.generate(declaration).single()

    println(file.code)
  }
}
