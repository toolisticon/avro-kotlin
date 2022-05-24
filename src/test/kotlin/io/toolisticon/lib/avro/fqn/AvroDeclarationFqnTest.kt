package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path

internal class AvroDeclarationFqnTest {

  @Test
  fun `fqn from path`() {
    val schemaFqn = AvroKotlinLib.schema(TestFixtures.fqnBankAccountCreated)
    val schemaPath: Path = schemaFqn.path

    val fqn: AvroDeclarationFqn = schemaPath.toFqn()

    assertThat(fqn.namespace).isEqualTo(schemaFqn.namespace)
    assertThat(fqn.fileExtension).isEqualTo(schemaFqn.fileExtension)
    assertThat(fqn.name).isEqualTo(schemaFqn.name)
  }
}
