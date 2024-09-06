package io.toolisticon.kotlin.avro.generator.spi

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.TestFixtures
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext.Companion.toSchemaDeclarationContext
import org.junit.jupiter.api.Test

class ProtocolDeclarationContextTest {

  @Test
  fun `convert protocolContext to schemaContext`() {
    val declaration: ProtocolDeclaration = TestFixtures.PARSER.parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

    val protocolContext = ProtocolDeclarationContext.of(declaration, TestFixtures.DEFAULT_REGISTRY, TestFixtures.DEFAULT_PROPERTIES)

    val schemaContext = protocolContext.toSchemaDeclarationContext()

    println(schemaContext)
  }
}
