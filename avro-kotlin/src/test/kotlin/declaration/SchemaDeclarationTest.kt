package io.toolisticon.kotlin.avro.declaration

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.TestFixtures
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.avro.model.IntType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.StringType
import io.toolisticon.kotlin.avro.value.Directory
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.isRegularFile

class SchemaDeclarationTest {

  private val parser = AvroParser()

  @Test
  fun `load org_apache_avro_schema_foo`() {
    val declaration = parser.parseSchema(resourceUrl("org.apache.avro/schema/foo.avsc"))

    assertThat(declaration.canonicalName.fqn).isEqualTo("org.foo.Foo")
    assertThat(declaration.name).isEqualTo(Name("Foo"))
    assertThat(declaration.namespace).isEqualTo(Namespace("org.foo"))
    assertThat(declaration.recordType.fields).hasSize(1)
    assertThat(declaration.recordType.fields[0].name).isEqualTo(Name("x"))
    assertThat(declaration.recordType.fields[0].type).isInstanceOf(IntType::class.java)
  }

  @Test
  fun `load org_apache_avro_schema_string_logical_type`() {
    val resource = resourceUrl("org.apache.avro/schema/string_logical_type.avsc")

    val schema = parser.parseSchema(resource)

    // two string types
    assertThat(schema.avroTypes.findTypes<StringType>()).hasSize(2)

    // only one has logicalType
    assertThat(schema.avroTypes.findTypes<StringType> { it.hasLogicalType() }).hasSize(1)
  }

  @Test
  fun `with reuse of type`() {
    val resource = resourceUrl("schema/ReUsingTypes.avsc")
    val schema = parser.parseSchema(resource)

    assertThat(schema.avroTypes.findTypes<RecordType>()).hasSize(3)
    assertThat(schema.avroTypes.findTypes<EnumType>()).hasSize(1)
  }

  @Test
  fun `load from file`(@TempDir tmpPath: Path) {
    val tmp = Directory(tmpPath)
    val schemaFoo = parser.parseSchema(TestFixtures.schemaFoo.get())
    val schemaBar = parser.parseSchema(TestFixtures.schemaBar.get())

    tmp.write(schemaFoo)
    tmp.write(schemaBar)

    val files = tmp.walk()
      .filter { it.isRegularFile() }
      .toList()
      .fold(mutableListOf<Path>()) {a,c -> a.apply { add(c) }}

    assertThat(files).hasSize(2)
  }
}
