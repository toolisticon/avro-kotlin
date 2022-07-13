package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.AvroKotlinLib.EXTENSION_SCHEMA
import nl.jqno.equalsverifier.EqualsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class GenericAvroDeclarationFqnTest {

  @Test
  fun `verify toString`() {
    val fqn = GenericAvroDeclarationFqn("io.acme.foo", "Dummy", EXTENSION_SCHEMA)

    assertThat(fqn.toString()).isEqualTo("GenericAvroDeclarationFqn(namespace='io.acme.foo', name='Dummy', fileExtension='avsc')")
  }

  @Test
  fun `verify equals and hashcode`() {
    EqualsVerifier.simple()
      .forClass(GenericAvroDeclarationFqn::class.java)
      .withIgnoredFields( "path\$delegate")
      .withIgnoredFields( "type\$delegate")
      .verify()
  }

  @Test
  fun `create from path with root`() {
    val root = Path("/target/test-classes/avro")
    val path = Path("/target/test-classes/avro/lib/test/event/BankAccountCreated.avsc")

    val fqn = GenericAvroDeclarationFqn.fromPath(path, root)

    assertThat(fqn.namespace).isEqualTo("lib.test.event")
    assertThat(fqn.name).isEqualTo("BankAccountCreated")
    assertThat(fqn.fileExtension).isEqualTo("avsc")
    assertThat(fqn.type).isEqualTo(AvroKotlinLib.Declaration.SCHEMA)

  }

  @Test
  fun `create from path`() {
    val path = Path("lib/test/event/BankAccountCreated.avsc")

    val fqn = GenericAvroDeclarationFqn.fromPath(path)

    assertThat(fqn.namespace).isEqualTo("lib.test.event")
    assertThat(fqn.name).isEqualTo("BankAccountCreated")
    assertThat(fqn.fileExtension).isEqualTo("avsc")
  }
}
