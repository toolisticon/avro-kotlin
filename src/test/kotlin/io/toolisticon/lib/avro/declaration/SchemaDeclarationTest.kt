package io.toolisticon.lib.avro.declaration

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.TestFixtures
import io.toolisticon.lib.avro.TestFixtures.simpleStringValueSchema
import io.toolisticon.lib.avro.ext.SchemaExt.writeToDirectory
import io.toolisticon.lib.avro.fqn.AvroDeclarationMismatchException
import io.toolisticon.lib.avro.fqn.SchemaFqn
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files

internal class SchemaDeclarationTest {

  @TempDir
  private lateinit var tmp: File

  @Test
  fun `create contentFqn from fqn and content`() {
    val namespace = "com.acme.test"
    val name = "Foo"

    val fqn = SchemaFqn(namespace, name)
    val schema = simpleStringValueSchema(namespace, name)

    val declaration = SchemaDeclaration(fqn, schema)

    assertThat(declaration.contentFqn).isEqualTo(fqn)
    assertThat(declaration.verifyPackageConvention()).isTrue
    assertThat(declaration.name).isEqualTo(name)
    assertThat(declaration.namespace).isEqualTo(namespace)
    assertThat(declaration.canonicalName).isEqualTo("$namespace.$name")
  }

  @Test
  fun `create contentFqn from schema`() {
    val namespace = "com.acme.test"
    val name = "Foo"

    val schema = simpleStringValueSchema(namespace, name)

    val declaration = SchemaDeclaration(schema)

    assertThat(declaration.contentFqn).isEqualTo(SchemaFqn(namespace,name))
    assertThat(declaration.verifyPackageConvention()).isTrue
    assertThat(declaration.name).isEqualTo(name)
    assertThat(declaration.namespace).isEqualTo(namespace)
    assertThat(declaration.canonicalName).isEqualTo("$namespace.$name")
  }

  @Test
  fun `violating package convention`() {
    val namespace = "com.acme.test"
    val name = "Foo"

    val differentLocation = SchemaFqn("foo", "Bar")
    val schema = simpleStringValueSchema(namespace, name)

    val declaration = SchemaDeclaration(differentLocation, schema)

    assertThatThrownBy { declaration.verifyPackageConvention() }
      .isInstanceOf(AvroDeclarationMismatchException::class.java)

  }

  @Test
  fun `package convention mismatch`() {
    val namespace = "com.acme.test"
    val name = "Foo"

    val fqn = SchemaFqn(namespace+".foo", name)
    val schema = simpleStringValueSchema(namespace, name)

    val declaration = SchemaDeclaration(fqn, schema)

    assertThat(declaration.contentFqn.namespace).isEqualTo(namespace)
    assertThat(declaration.contentFqn.name).isEqualTo(name)
    assertThat(declaration.contentFqn.fileExtension).isEqualTo(AvroKotlinLib.EXTENSION_SCHEMA)

    assertThatThrownBy { declaration.verifyPackageConvention(true) }
      .isInstanceOf(AvroDeclarationMismatchException::class.java)
  }

  @Test
  fun `load from file`() {
    val schemaFoo = TestFixtures.schemaFoo
    val schemaBar = TestFixtures.schemaBar

    schemaFoo.writeToDirectory(tmp)
    schemaBar.writeToDirectory(tmp)

    Files.walk(tmp.toPath())
      .forEach { println(it) }


  }
}
