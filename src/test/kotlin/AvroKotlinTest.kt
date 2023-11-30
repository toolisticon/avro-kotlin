package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.SchemaKtx.writeToDirectory
import io.toolisticon.avro.kotlin.AvroKotlin.Separator.dashToDot
import io.toolisticon.avro.kotlin.AvroKotlin.Separator.dotToDash
import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.value.AvroSpecification
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File

internal class AvroKotlinTest {

  @TempDir
  private lateinit var tmpDir: File

  @Test
  @Disabled("fix protocol")
  fun `can load protocol from resources`() {
    val cn = TestFixtures.fqnFindCurrentBalance

//    val protocol: Protocol = fqn.fromResource(prefix = "avro")
//
//    assertThat((protocol.namespace)).isEqualTo(fqn.namespace)
//    assertThat((protocol.name)).isEqualTo(fqn.name)
  }


  @Test
  @Disabled("remove")
  @Deprecated("remove")
  fun `can load schema from resources`() {
    val cn = TestFixtures.fqnBankAccountCreated
    val fqn = TODO() //schema(TestFixtures.fqnBankAccountCreated)

    val schema: Schema = TODO() //fqn.fromResource(prefix = "avro")

    assertThat(schema).isNotNull
//    assertThat((schema.namespace)).isEqualTo(fqn.namespace.value)
//    assertThat((schema.name)).isEqualTo(fqn.name.value)
  }

  @Test
  @Disabled("remove")
  @Deprecated("remove")
  fun `can write schema to file (and read again)`() {
    val cn = TestFixtures.fqnBankAccountCreated
//    val fqn: SchemaFqn = TODO() //schema(TestFixtures.fqnBankAccountCreated)
//
//    assertThat(fqn.fileExtension).isEqualTo("avsc")
//
//    // copy resource to tmp file
//    val schema = fqn.fromResource("avro")
//
//    val file: Path = schema.writeToDirectory(tmpDir)
//    assertThat(file).exists()
//
//    // read from tmp file
//    val readFromFile = fqn.fromDirectory(tmpDir)
//
//    assertThat(readFromFile).isEqualTo(schema)
  }

  @Test
  @Disabled("remove")
  @Deprecated("remove")
  fun `can write protocol to file (and read again)`() {
    val cn = TestFixtures.fqnFindCurrentBalance
//    val fqn: ProtocolFqn = TODO() //protocol(TestFixtures.fqnFindCurrentBalance)
//
//    assertThat(fqn.fileExtension).isEqualTo("avpr")
//
//    // copy resource to tmp file
//    val protocol = fqn.fromResource("avro")
//
//    val file: Path = protocol.writeToDirectory(tmpDir)
//    assertThat(file).exists()
//
//
//    // read from tmp file
//    val readFromFile = fqn.fromDirectory(tmpDir)
//
//    assertThat(readFromFile).isEqualTo(protocol)
  }

  @Test
  @Disabled("remove")
  @Deprecated("remove")
  fun `test mismatch exception`() {
//
//    assertThatThrownBy {
//      AvroKotlin.verifyPackagePathConvention(
//        SchemaFqn(Namespace("foo"), Name("Baz")),
//        SchemaFqn(Namespace("foo.bar"), Name("Baz"))
//      )
//    }.isInstanceOf(AvroDeclarationMismatchException::class.java)
//      .hasMessage("violation of package-path convention: found declaration fqn='foo.Baz' but was loaded from path='foo/bar/Baz.avsc'")
  }

  @Test
  fun `find declaration by extension`() {
    assertThat(AvroSpecification.valueOfExtension("avsc")).isEqualTo(AvroSpecification.SCHEMA)
    assertThat(AvroSpecification.valueOfExtension("avpr")).isEqualTo(AvroSpecification.PROTOCOL)

    assertThatThrownBy { AvroSpecification.valueOfExtension("foo") }
      .isInstanceOf(java.lang.IllegalArgumentException::class.java)
      .hasMessage("Not a valid extension='foo', must be one of: [avsc, avpr].")
  }

  @Test
  @Disabled("remove")
  @Deprecated("remove")
  fun `find all schemaDeclarations`() {


    TestFixtures.schemaFoo.writeToDirectory(tmpDir)
    TestFixtures.schemaBar.writeToDirectory(tmpDir)

//    val found = AvroKotlin.findDeclarations(tmpDir.toPath())
//
//    assertThat(found).hasSize(2)
  }

  @Nested
  inner class SeparatorTest {

    @Test
    fun `replace dots and dashes`() {
      val dots = "io.acme.bar.Foo"
      val dashes = dotToDash(dots)

      assertThat(dashes).isEqualTo("io/acme/bar/Foo")
      assertThat(dashToDot(dashes)).isEqualTo(dots)
    }
  }

  @Nested
  inner class SpringKtxTest {
    @ParameterizedTest
    @CsvSource(value = [
      "null,null",
      ",null",
      " ,null",
      " h ,h",
    ], nullValues = ["null"])
    fun `String_trimToNull`(input: String?, expected: String?) {
      assertThat(input.trimToNull()).isEqualTo(expected)
    }
  }
}
