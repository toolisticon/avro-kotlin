package io.toolisticon.kotlin.avro.declaration

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.TestFixtures
import io.toolisticon.kotlin.avro._test.CustomLogicalTypeFactory
import io.toolisticon.kotlin.avro.model.*
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

    //assertThat(declaration.avroTypes).hasSize(9)
    assertThat(declaration.avroTypes).hasSize(7)
    //assertThat(declaration.avroTypes.byType).hasSize(5)
    assertThat(declaration.avroTypes.byType).hasSize(3)

    assertThat(declaration.avroTypes.findTypes<RecordType>()).hasSize(3)
    assertThat(declaration.avroTypes.findTypes<StringType>()).hasSize(3)
    assertThat(declaration.avroTypes.findTypes<ErrorType>()).hasSize(1)
    assertThat(declaration.avroTypes.findTypes<UnionType>()).isEmpty()
    assertThat(declaration.avroTypes.findTypes<NullType>()).isEmpty()
  }
}
