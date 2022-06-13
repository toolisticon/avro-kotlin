package io.toolisticon.lib.avro.ext

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ResourceExtTest {

  @Test
  fun `get root of resources`() {
    val root = ResourceExt.rootResource().path

    assertThat(root).endsWith("/target/test-classes/")
  }

}
