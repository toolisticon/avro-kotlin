package io.toolisticon.avro.kotlin.ext

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.ResourceKtx.rootResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ResourceExtTest {

  @Test
  fun `get root of resources`() {
    val root = rootResource().path

    assertThat(root).endsWith("/target/test-classes/")
  }

  @Test
  @Disabled("remove")
  @Deprecated("remove")
  fun `find all avro resources`() {
    //findAvroResources(prefix = "avro")
  }
}
