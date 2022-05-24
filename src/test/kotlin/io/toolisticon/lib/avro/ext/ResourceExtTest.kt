package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.AvroKotlinLib.schema
import io.toolisticon.lib.avro.TestFixtures
import io.toolisticon.lib.avro.fqn.fromResource
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ResourceExtTest {

  @Test
  fun `get root of resources`() {
    val root = ResourceExt.rootResource().path

    assertThat(root).endsWith("/target/test-classes/")
  }

  @Test
  @Deprecated("playing around")
  fun name() {
    val schema: Schema = schema(TestFixtures.fqnBankAccountCreated).fromResource("avro")


    println(schema.objectProps)
  }
}
