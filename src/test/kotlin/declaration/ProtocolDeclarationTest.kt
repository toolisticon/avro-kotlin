package io.toolisticon.avro.kotlin.declaration

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.AvroParser
import io.toolisticon.avro.kotlin._test.CustomLogicalTypeFactory
import io.toolisticon.avro.kotlin.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProtocolDeclarationTest {

  @Test
  fun `construct declaration from resource`() {

    val declaration = AvroParser()
      .registerLogicalTypeFactory(CustomLogicalTypeFactory())
      .parseProtocol(
        resourceUrl("protocol/DummyProtocol.avpr")
      )

    assertThat(declaration.avroTypes).hasSize(9)
    assertThat(declaration.avroTypes.byType).hasSize(5)

    assertThat(declaration.avroTypes.findTypes<RecordType>()).hasSize(3)
    assertThat(declaration.avroTypes.findTypes<StringType>()).hasSize(3)
    assertThat(declaration.avroTypes.findTypes<ErrorType>()).hasSize(1)
    assertThat(declaration.avroTypes.findTypes<UnionType>()).hasSize(1)
    assertThat(declaration.avroTypes.findTypes<NullType>()).hasSize(1)
  }
}
