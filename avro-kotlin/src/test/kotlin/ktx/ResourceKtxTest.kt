package io.toolisticon.avro.kotlin.ktx

import _ktx.ResourceKtx
import io.toolisticon.avro.kotlin.value.AvroSpecification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

internal class ResourceKtxTest {

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `get resource url`() {
    val file = "/protocol/DummyProtocol.avpr"

    val url = ResourceKtx.resourceUrl(file)

    assertThat(url.toString()).endsWith(file)
  }


  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `get root of resources`() {
    val root = ResourceKtx.rootResource().path

    assertThat(root).endsWith("/target/test-classes/")
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `find all avro resources`() {
    val resources = ResourceKtx.findAvroResources()
    assertThat(resources.keys).containsExactlyInAnyOrder(AvroSpecification.PROTOCOL, AvroSpecification.SCHEMA)
    assertThat(resources[AvroSpecification.SCHEMA]).hasSize(56)
    assertThat(resources[AvroSpecification.PROTOCOL]).hasSize(26)
  }
}
