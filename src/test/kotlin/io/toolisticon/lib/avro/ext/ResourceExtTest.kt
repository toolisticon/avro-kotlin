package io.toolisticon.lib.avro.ext

import io.toolisticon.avro.kotlin.ktx.findAvroResources
import io.toolisticon.avro.kotlin.ktx.rootResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ResourceExtTest {

  @Test
  fun `get root of resources`() {
    val root = rootResource().path

    assertThat(root).endsWith("/target/test-classes/")
  }

  @Test
  fun `find all avro resources`() {
    findAvroResources(prefix = "avro")
  }
}
