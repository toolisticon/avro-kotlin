package io.toolisticon.avro.kotlin.ktx

import _ktx.ResourceKtx
import io.toolisticon.avro.kotlin.value.AvroSpecification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ResourceKtxTest {

  @Test
  fun `get resource url`() {
    val file = "/protocol/DummyProtocol.avpr"

    val url = ResourceKtx.resourceUrl(file)

    assertThat(url.toString()).endsWith(file)
  }


  @Test
  fun `get root of resources`() {
    val root = ResourceKtx.rootResource().path

    assertThat(root).endsWith("/target/test-classes/")
  }

  @Test
  fun `find all avro resources`() {
    val resources = ResourceKtx.findAvroResources()
    assertThat(resources.keys).containsExactlyInAnyOrder(AvroSpecification.PROTOCOL, AvroSpecification.SCHEMA)
    assertThat(resources[AvroSpecification.SCHEMA]).hasSize(56)
    assertThat(resources[AvroSpecification.PROTOCOL]).hasSize(26)
  }
}
