package io.toolisticon.lib.avro

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.EXTENSION_PROTOCOL
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.EXTENSION_SCHEMA
import io.toolisticon.avro.kotlin.AvroKotlin.fqn
import io.toolisticon.avro.kotlin.AvroKotlin.protocol
import io.toolisticon.avro.kotlin.AvroKotlin.schema
import io.toolisticon.avro.kotlin._bak.*
import io.toolisticon.avro.kotlin._bak.ProtocolExt.writeToDirectory
import io.toolisticon.avro.kotlin._bak.SchemaExt.writeToDirectory
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

internal class AvroKotlinTest {

  @TempDir
  private lateinit var tmpDir: File

  @Test
  fun `can load protocol from resources`() {
    val fqn: ProtocolFqn = protocol(TestFixtures.fqnFindCurrentBalance)

    val protocol: Protocol = fqn.fromResource(prefix = "avro")

    assertThat((protocol.namespace)).isEqualTo(fqn.namespace)
    assertThat((protocol.name)).isEqualTo(fqn.name)
  }


  @Test
  fun `can load schema from resources`() {
    val fqn = schema(TestFixtures.fqnBankAccountCreated)

    val schema: Schema = fqn.fromResource(prefix = "avro")

    assertThat(schema).isNotNull
    assertThat((schema.namespace)).isEqualTo(fqn.namespace)
    assertThat((schema.name)).isEqualTo(fqn.name)
  }

  @Test
  fun `concat canonicalName`() {
    assertThat(fqn(Namespace("foo.bar"), Name("HelloWorld")).canonicalName).isEqualTo("foo.bar.HelloWorld")
  }

  @Test
  fun `can write schema to file (and read again)`() {
    val fqn: SchemaFqn = schema(TestFixtures.fqnBankAccountCreated)

    assertThat(fqn.fileExtension).isEqualTo("avsc")

    // copy resource to tmp file
    val schema = fqn.fromResource("avro")

    val file: Path = schema.writeToDirectory(tmpDir)
    assertThat(file).exists()

    // read from tmp file
    val readFromFile = fqn.fromDirectory(tmpDir)

    assertThat(readFromFile).isEqualTo(schema)
  }

  @Test
  fun `can write protocol to file (and read again)`() {
    val fqn: ProtocolFqn = protocol(TestFixtures.fqnFindCurrentBalance)

    assertThat(fqn.fileExtension).isEqualTo("avpr")

    // copy resource to tmp file
    val protocol = fqn.fromResource("avro")

    val file: Path = protocol.writeToDirectory(tmpDir)
    assertThat(file).exists()


    // read from tmp file
    val readFromFile = fqn.fromDirectory(tmpDir)

    assertThat(readFromFile).isEqualTo(protocol)
  }

  @Test
  @ExperimentalStdlibApi
  fun `header bytes C3 01`() {
    assertThat(AvroKotlin.Constants.AVRO_V1_HEADER.toHexString()).isEqualTo("[C3 01]")
  }


  @Test
  fun `test mismatch exception`() {

    assertThatThrownBy {
      AvroKotlin.verifyPackagePathConvention(
        SchemaFqn(Namespace("foo"), Name("Baz")),
        SchemaFqn(Namespace("foo.bar"), Name("Baz"))
      )
    }.isInstanceOf(AvroDeclarationMismatchException::class.java)
      .hasMessage("violation of package-path convention: found declaration fqn='foo.Baz' but was loaded from path='foo/bar/Baz.avsc'")
  }

  @Test
  fun `find declaration by extension`() {
    assertThat(AvroKotlin.Declaration.byExtension(EXTENSION_SCHEMA)).isEqualTo(AvroKotlin.Declaration.SCHEMA)
    assertThat(AvroKotlin.Declaration.byExtension(EXTENSION_PROTOCOL)).isEqualTo(AvroKotlin.Declaration.PROTOCOL)

    assertThatThrownBy { AvroKotlin.Declaration.byExtension("foo") }
      .isInstanceOf(java.lang.IllegalArgumentException::class.java)
      .hasMessage("illegal file extension='foo'")
  }

  @Test
  fun `find all schemaDeclarations`() {
    TestFixtures.schemaFoo.writeToDirectory(tmpDir)
    TestFixtures.schemaBar.writeToDirectory(tmpDir)

    val found = AvroKotlin.findDeclarations(tmpDir.toPath())

    assertThat(found).hasSize(2)
  }
}
