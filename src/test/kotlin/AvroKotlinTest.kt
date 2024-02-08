package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.AvroKotlin.SchemaKtx.writeToDirectory
import io.toolisticon.avro.kotlin.AvroKotlin.createGenericRecord
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.Directory
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class AvroKotlinTest {

  @TempDir
  private lateinit var tmpDir: File

  private val directory: Directory by lazy {
    Directory(tmpDir)
  }

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
  fun `can write schema to file (and read again)`() {
    val schema = AvroParser().parseSchema(resourceUrl("schema/SimpleStringRecord.avsc"))

    AvroKotlin.write(schema, directory)

    val readFromFile = AvroParser().parseSchema(tmpDir.resolve("io/acme/schema/SimpleStringRecord.avsc"))

    assertThat(readFromFile).isEqualTo(schema)
  }

  @Test
  fun `can write protocol to file (and read again)`() {
    val protocol = AvroParser().parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

    AvroKotlin.write(protocol, directory)

    val readFromFile = AvroParser().parseProtocol(tmpDir.resolve("foo/dummy/DummyProtocol.avpr"))

    assertThat(readFromFile).isEqualTo(protocol)
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
  @Disabled("remove")
  @Deprecated("remove")
  fun `find all schemaDeclarations`() {


    TestFixtures.schemaFoo.writeToDirectory(tmpDir)
    TestFixtures.schemaBar.writeToDirectory(tmpDir)

//    val found = AvroKotlin.findDeclarations(tmpDir.toPath())
//
//    assertThat(found).hasSize(2)
  }

  @Test
  fun `create generic record`() {
    val schemaFoo: AvroSchema = AvroSchema(
      SchemaBuilder
        .record("Foo")
        .namespace("test.lib")
        .fields().name("value").type("int")
        .noDefault()
        .endRecord()
    )


    val record: GenericData.Record = createGenericRecord(schemaFoo) {
      put("value", 1)
    }

    assertThat(record["value"]).isEqualTo(1)
    assertThat(record.schema).isEqualTo(schemaFoo.get())
  }
}
