package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib.EXTENSION_SCHEMA
import nl.jqno.equalsverifier.EqualsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultAvroDeclarationFqnTest {

  @Test
  fun `verify toString`() {
    val fqn = DefaultAvroDeclarationFqn("io.acme.foo", "Dummy", EXTENSION_SCHEMA)

    assertThat(fqn.toString()).isEqualTo("DefaultAvroDeclarationFqn(namespace='io.acme.foo', name='Dummy', fileExtension='avsc')")
  }

  @Test
  fun `verify equals and hashcode`() {
    EqualsVerifier.simple()
      .forClass(DefaultAvroDeclarationFqn::class.java)
      .withIgnoredFields( "path\$delegate")
      .verify()
  }
}
