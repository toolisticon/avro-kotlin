package io.toolisticon.kotlin.avro.repository

import io.toolisticon.kotlin.avro.TestFixtures.loadAvroSchema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class MutableAvroSchemaResolverTest {

  @Test
  fun `empty instance`() {
    assertThat(MutableAvroSchemaResolver.EMPTY).isEmpty()
  }

  @Test
  fun `init with single schema`() {
    val schema = loadAvroSchema("avro/lib/test/event/BankAccountCreated.avsc")
    assertThat(MutableAvroSchemaResolver(schema)).hasSize(1)
  }

  @Test
  fun `add schema`() {
    val schema = loadAvroSchema("avro/lib/test/event/BankAccountCreated.avsc")
    val resolver = MutableAvroSchemaResolver(schema)
    assertThat(resolver).hasSize(1)
    resolver.plus(loadAvroSchema("avro/lib/test/dummy/NestedDummy.avsc"))
    assertThat(resolver).hasSize(2)
  }
}
