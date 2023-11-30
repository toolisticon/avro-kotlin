package io.toolisticon.lib.avro.fqn

import io.toolisticon.avro.kotlin.AvroKotlin.schema
import io.toolisticon.avro.kotlin._bak.AvroDeclarationMismatchException
import io.toolisticon.avro.kotlin._bak.SchemaExt.writeToDirectory
import io.toolisticon.avro.kotlin._bak.SchemaFqn
import io.toolisticon.avro.kotlin._bak.fromDirectory
import io.toolisticon.avro.kotlin._bak.fromResource
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace
import io.toolisticon.lib.avro.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path

internal class SchemaFqnTest {

  @TempDir
  lateinit var tmpDir: File

  @Test
  fun `toString contains namespace name and extension`() {
    val fqn = SchemaFqn(namespace = Namespace("com.acme.foo"), name = Name("MySchema"))

    assertThat(fqn.toString()).isEqualTo("SchemaFqn(namespace='com.acme.foo', name='MySchema', fileExtension='avsc')")
  }

  @Test
  fun `fail if file path does not match schemaFqn path`() {
    val origFqn = schema(TestFixtures.fqnBankAccountCreated)
    val origSchema = origFqn.fromResource("avro")

    // we write the content to the wrong path "foo/bar"
    val file = origSchema.writeToDirectory(tmpDir, Path("foo/bar/BankAccountCreated.avsc"))
    assertThat(file).exists()
    assertThat(file).isFile

    val fqnWithWrongNamespace = SchemaFqn(namespace = Namespace("foo.bar"), name = Name("BankAccountCreated"))

    assertThatThrownBy {
      fqnWithWrongNamespace.fromDirectory(tmpDir)
    }.isInstanceOf(AvroDeclarationMismatchException::class.java)
      .hasMessageContaining("violation of package-path convention: found declaration fqn='lib.test.event.BankAccountCreated' but was loaded from path='foo/bar/BankAccountCreated.avsc'")

    // if we disable the check, it can be loaded
    val ignoreMismatchSchema = fqnWithWrongNamespace.fromDirectory(tmpDir, false)
    assertThat(ignoreMismatchSchema).isEqualTo(origSchema)
  }
}
