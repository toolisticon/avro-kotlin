package io.toolisticon.avro.kotlin.declaration

import io.toolisticon.avro.kotlin.AvroKotlin.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.AvroParser
import io.toolisticon.avro.kotlin.TestFixtures.loadSchema
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SchemaDeclarationTest {

  private val parser = AvroParser()

  @Test
  fun `load org_apache_avro_schema_foo`() {
    val declaration = parser.parseSchema(resourceUrl("org.apache.avro/schema/foo.avsc"))
//TODO: add tests

    assertThat(declaration.canonicalName).hasToString("org.foo.Foo")
    assertThat(declaration.name).isEqualTo(Name("Foo"))
    assertThat(declaration.namespace).isEqualTo(Namespace("org.foo"))
    assertThat(declaration.recordType.fields).hasSize(1)
    assertThat(declaration.recordType.fields[0].name).isEqualTo(Name("x"))
    //assertThat(declaration.recordType.fields[0].type).isEqualTo(Schema.Type.INT)

    println(declaration)
    //println(declaration.avroTypes.toReadableString())
  }

  @Test
  fun `load org_apache_avro_schema_string_logical_type`() {
    val schema = loadSchema("org.apache.avro/schema/string_logical_type.avsc")

    //println(schema)
//    val declaration = SchemaDeclaration(schema)
//
//    println(declaration.avroTypes.toReadableString())

    //println(declaration)

    // 2 primitive types (both string, although schema defines 3 fields)
//    val primitiveTypes = declaration.primitiveTypes
//    assertThat(primitiveTypes).hasSize(2)
//
//    // but only one with logical type
//    assertThat(declaration.findTypes(AvroPrimitiveType::class) { it is WithLogicalType && it.hasLogicalType() })
//      .hasSize(1)
  }

  @Test
  fun `with reuse of type`() {
    val schema = loadSchema("schema/ReUsingTypes.avsc")
//
//    val declaration = SchemaDeclaration(schema)
//
//    declaration.avroTypes.entries.forEach {
//      println(
//        """
//
//        ${it.key}:
//           ${it.value}
//
//      """.trimIndent()
//      )
  }

}



