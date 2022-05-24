package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib.schema
import io.toolisticon.lib.avro.TestFixtures
import io.toolisticon.lib.avro.exception.SchemaFqnMismatchException
import io.toolisticon.lib.avro.ext.SchemaExt.writeToDirectory
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
    val fqn = SchemaFqn(namespace = "com.acme.foo", name = "MySchema")

    assertThat(fqn.toString()).isEqualTo("SchemaFqn(namespace='com.acme.foo', name='MySchema', extension='avsc')")
  }

  @Test
  fun `fail if file path does not match schemaFqn path`() {
    val origFqn = schema(TestFixtures.fqnBankAccountCreated)
    val origSchema = origFqn.fromResource("avro")

    // we write the content to the wrong path "foo/bar"
    val file = origSchema.writeToDirectory(tmpDir, Path("foo/bar/BankAccountCreated.avsc"))
    assertThat(file).exists()
    assertThat(file).isFile

    val fqnWithWrongNamespace = SchemaFqn(namespace = "foo.bar", name = "BankAccountCreated")

    assertThatThrownBy {
      fqnWithWrongNamespace.fromDirectory(tmpDir)
    }.isInstanceOf(SchemaFqnMismatchException::class.java)

    // if we disable the check, it can be loaded
    val ignoreMismatchSchema = fqnWithWrongNamespace.fromDirectory(tmpDir, false)
    assertThat(ignoreMismatchSchema).isEqualTo(origSchema)
  }
}
