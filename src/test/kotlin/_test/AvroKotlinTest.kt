package io.toolisticon.avro.kotlin._test

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.fqn
import io.toolisticon.avro.kotlin.AvroKotlin.protocol
import io.toolisticon.avro.kotlin.AvroKotlin.schema
import io.toolisticon.avro.kotlin._bak.*
import io.toolisticon.avro.kotlin.ktx.writeToDirectory
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.HexString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

internal class AvroKotlinTest {

  @TempDir
  private lateinit var tmpDir: File

  @Test
  @Disabled("fix protocol")
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
    assertThat((schema.namespace)).isEqualTo(fqn.namespace.value)
    assertThat((schema.name)).isEqualTo(fqn.name.value)
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
  fun `header bytes C3 01`() {
    assertThat(HexString(AvroKotlin.Constants.AVRO_V1_HEADER).formatted).isEqualTo("[C3 01]")
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
    assertThat(AvroSpecification.valueOfExtension("avsc")).isEqualTo(AvroSpecification.SCHEMA)
    assertThat(AvroSpecification.valueOfExtension("avpr")).isEqualTo(AvroSpecification.PROTOCOL)

    assertThatThrownBy { AvroSpecification.valueOfExtension("foo") }
      .isInstanceOf(java.lang.IllegalArgumentException::class.java)
      .hasMessage("Not a valid extension='foo', must be one of: [avsc, avpr].")
  }

  @Test
  fun `find all schemaDeclarations`() {
    TestFixtures.schemaFoo.writeToDirectory(tmpDir)
    TestFixtures.schemaBar.writeToDirectory(tmpDir)

    val found = AvroKotlin.findDeclarations(tmpDir.toPath())

    assertThat(found).hasSize(2)
  }
}
