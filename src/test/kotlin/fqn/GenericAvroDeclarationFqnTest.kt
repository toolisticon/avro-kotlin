package io.toolisticon.avro.kotlin.fqn

import io.toolisticon.avro.kotlin.AvroKotlin.Constants.EXTENSION_SCHEMA
import io.toolisticon.avro.kotlin._bak.GenericAvroDeclarationFqn
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import nl.jqno.equalsverifier.EqualsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class GenericAvroDeclarationFqnTest {

  @Test
  fun `verify toString`() {
    val fqn = GenericAvroDeclarationFqn(Namespace("io.acme.foo"), Name("Dummy"), EXTENSION_SCHEMA)

    assertThat(fqn.toString()).isEqualTo("GenericAvroDeclarationFqn(namespace='io.acme.foo', name='Dummy', fileExtension='avsc')")
  }

  @Test
  fun `verify equals and hashcode`() {
    EqualsVerifier.simple()
      .forClass(GenericAvroDeclarationFqn::class.java)
      .withIgnoredFields("path\$delegate")
      .withIgnoredFields("type\$delegate")
      .verify()
  }

  @Test
  fun `create from path with root`() {
    val root = Path("/target/test-classes/avro")
    val path = Path("/target/test-classes/avro/lib/test/event/BankAccountCreated.avsc")

    val fqn = GenericAvroDeclarationFqn.fromPath(path, root)

    assertThat(fqn.namespace.value).isEqualTo("lib.test.event")
    assertThat(fqn.name.value).isEqualTo("BankAccountCreated")
    assertThat(fqn.fileExtension).isEqualTo("avsc")
    assertThat(fqn.type).isEqualTo(AvroSpecification.SCHEMA)

  }

  @Test
  fun `create from path`() {
    val path = Path("lib/test/event/BankAccountCreated.avsc")

    val fqn = GenericAvroDeclarationFqn.fromPath(path)

    assertThat(fqn.namespace.value).isEqualTo("lib.test.event")
    assertThat(fqn.name.value).isEqualTo("BankAccountCreated")
    assertThat(fqn.fileExtension).isEqualTo("avsc")
  }
}
