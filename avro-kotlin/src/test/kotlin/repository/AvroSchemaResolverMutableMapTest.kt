package io.toolisticon.kotlin.avro.repository

import io.toolisticon.kotlin.avro.TestFixtures.loadAvroSchema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class AvroSchemaResolverMutableMapTest {

  @Test
  fun `empty instance`() {
    assertThat(AvroSchemaResolverMutableMap.EMPTY).isEmpty()
  }

  @Test
  fun `init with single schema`() {
    val schema = loadAvroSchema("avro/lib/test/event/BankAccountCreated.avsc")
    assertThat(AvroSchemaResolverMutableMap(schema)).hasSize(1)
  }

  @Test
  fun `add schema`() {
    val schema = loadAvroSchema("avro/lib/test/event/BankAccountCreated.avsc")
    val resolver = AvroSchemaResolverMutableMap(schema)
    assertThat(resolver).hasSize(1)
    resolver.plus(loadAvroSchema("avro/lib/test/dummy/NestedDummy.avsc"))
    assertThat(resolver).hasSize(2)
  }
}
