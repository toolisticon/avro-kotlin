package io.toolisticon.avro.kotlin.fqn

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@Disabled("remove")
@Deprecated("remove")
internal class SchemaFqnTest {

  @TempDir
  lateinit var tmpDir: File

  @Test
  fun `fail if file path does not match schemaFqn path`() {
//    val cn = TestFixtures.fqnBankAccountCreated
//    val origFqn = schema(cn.namespace, cn.name)
//    val origSchema = origFqn.fromResource("avro")
//
//    // we write the content to the wrong path "foo/bar"
//    val file = origSchema.writeToDirectory(tmpDir, Path("foo/bar/BankAccountCreated.avsc"))
//    assertThat(file).exists()
//    assertThat(file).isFile
//
//    val fqnWithWrongNamespace = SchemaFqn(namespace = Namespace("foo.bar"), name = Name("BankAccountCreated"))
//
//    assertThatThrownBy {
//      fqnWithWrongNamespace.fromDirectory(tmpDir)
//    }.isInstanceOf(AvroDeclarationMismatchException::class.java)
//      .hasMessageContaining("violation of package-path convention: found declaration fqn='lib.test.event.BankAccountCreated' but was loaded from path='foo/bar/BankAccountCreated.avsc'")
//
//    // if we disable the check, it can be loaded
//    val ignoreMismatchSchema = fqnWithWrongNamespace.fromDirectory(tmpDir, false)
//    assertThat(ignoreMismatchSchema).isEqualTo(origSchema)
  }
}
