package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.TestFixtures.BankAccountCreatedFixtures.SCHEMA_BANK_ACCOUNT_CREATED
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import org.apache.avro.message.MissingSchemaException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AvroSchemaResolverTest {

  @Test
  fun `resolve single schema`() {
    val resolve = avroSchemaResolver(SCHEMA_BANK_ACCOUNT_CREATED)

    assertThat(resolve()).isEqualTo(SCHEMA_BANK_ACCOUNT_CREATED)
    assertThat(resolve[SCHEMA_BANK_ACCOUNT_CREATED.fingerprint]).isEqualTo(SCHEMA_BANK_ACCOUNT_CREATED)
    assertThat(resolve[AvroFingerprint.NULL]).isEqualTo(SCHEMA_BANK_ACCOUNT_CREATED)
    assertThat(resolve.findByFingerprint(SCHEMA_BANK_ACCOUNT_CREATED.fingerprint.value))
      .isEqualTo(SCHEMA_BANK_ACCOUNT_CREATED.get())

    assertThatThrownBy { resolve.findByFingerprint(1L) }
      .isInstanceOf(MissingSchemaException::class.java)
      .hasMessage("Cannot resolve schema for fingerprint: 0100000000000000[1]")
  }

  @Test
  fun `resolve single schema in list`() {
    val resolve = avroSchemaResolver(listOf(SCHEMA_BANK_ACCOUNT_CREATED))

    assertThat(resolve()).isEqualTo(SCHEMA_BANK_ACCOUNT_CREATED)
    assertThat(resolve[SCHEMA_BANK_ACCOUNT_CREATED.fingerprint]).isEqualTo(SCHEMA_BANK_ACCOUNT_CREATED)
    assertThat(resolve[AvroFingerprint.NULL]).isEqualTo(SCHEMA_BANK_ACCOUNT_CREATED)
  }

  @Test
  fun `resolve multiple schemas in list`() {
    val schema1 = SCHEMA_BANK_ACCOUNT_CREATED
    val schema2 = TestFixtures.schemaBar

    val resolve = avroSchemaResolver(listOf(schema1, schema2))

    assertThat(resolve[schema1.fingerprint]).isEqualTo(schema1)
    assertThat(resolve[schema2.fingerprint]).isEqualTo(schema2)

    assertThatThrownBy { resolve() }.isInstanceOf(MissingSchemaException::class.java)
  }

  @Test
  fun `resolve multiple schemas as first and other`() {
    val schema1 = SCHEMA_BANK_ACCOUNT_CREATED
    val schema2 = TestFixtures.schemaBar

    val resolve = avroSchemaResolver(schema1, schema2)

    assertThat(resolve[schema1.fingerprint]).isEqualTo(schema1)
    assertThat(resolve[schema2.fingerprint]).isEqualTo(schema2)

    assertThatThrownBy { resolve() }.isInstanceOf(MissingSchemaException::class.java)
  }


}
