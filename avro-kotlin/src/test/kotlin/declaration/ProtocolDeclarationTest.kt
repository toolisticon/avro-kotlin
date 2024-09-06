package io.toolisticon.kotlin.avro.declaration

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro._test.CustomLogicalTypeFactory
import io.toolisticon.kotlin.avro.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ProtocolDeclarationTest {

  @Test
  fun `construct declaration from resource`() {

    val declaration = AvroParser()
      .registerLogicalTypeFactory(CustomLogicalTypeFactory())
      .parseProtocol(
        resourceUrl("protocol/DummyProtocol.avpr")
      )

    assertAll(
      { assertThat(declaration.avroTypes).hasSize(9) },
      { assertThat(declaration.avroTypes.byType).hasSize(6) },
      { assertThat(declaration.avroTypes.findTypes<RecordType>()).hasSize(2) },
      { assertThat(declaration.avroTypes.findTypes<RequestType>()).hasSize(1) },
      { assertThat(declaration.avroTypes.findTypes<StringType>()).hasSize(3) },
      { assertThat(declaration.avroTypes.findTypes<ErrorType>()).hasSize(1) },
      { assertThat(declaration.avroTypes.findTypes<UnionType>()).hasSize(0) },
      { assertThat(declaration.avroTypes.findTypes<OptionalType>()).hasSize(1) },
      { assertThat(declaration.avroTypes.findTypes<NullType>()).hasSize(1) },
    )
  }
}
