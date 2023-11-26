package io.toolisticon.avro.kotlin.declaration

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin._bak.AvroDeclarationMismatchException
import io.toolisticon.avro.kotlin._bak.SchemaDeclaration
import io.toolisticon.avro.kotlin._bak.SchemaFqn
import io.toolisticon.avro.kotlin._test.TestFixtures
import io.toolisticon.avro.kotlin._test.TestFixtures.simpleStringValueSchema
import io.toolisticon.avro.kotlin.ktx.writeToDirectory
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
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

    val fqn = SchemaFqn(Namespace(namespace), Name(name))
    val schema = simpleStringValueSchema(Namespace(namespace), Name(name))

    val declaration = SchemaDeclaration(fqn, schema)

    assertThat(declaration.contentFqn).isEqualTo(fqn)
    assertThat(declaration.verifyPackageConvention()).isTrue
    assertThat(declaration.name.value).isEqualTo(name)
    assertThat(declaration.namespace.value).isEqualTo(namespace)
    assertThat(declaration.canonicalName).isEqualTo("$namespace.$name")
  }

  @Test
  fun `create contentFqn from schema`() {
    val namespace = "com.acme.test"
    val name = "Foo"

    val schema = simpleStringValueSchema(Namespace(namespace), Name(name))

    val declaration = SchemaDeclaration(schema)

    assertThat(declaration.contentFqn).isEqualTo(SchemaFqn(Namespace(namespace), Name(name)))
    assertThat(declaration.verifyPackageConvention()).isTrue
    assertThat(declaration.name.value).isEqualTo(name)
    assertThat(declaration.namespace.value).isEqualTo(namespace)
    assertThat(declaration.canonicalName).isEqualTo("$namespace.$name")
  }

  @Test
  fun `violating package convention`() {
    val namespace = "com.acme.test"
    val name = "Foo"

    val differentLocation = SchemaFqn(Namespace("foo"), Name("Bar"))
    val schema = simpleStringValueSchema(Namespace(namespace), Name(name))

    val declaration = SchemaDeclaration(differentLocation, schema)

    assertThatThrownBy { declaration.verifyPackageConvention() }
      .isInstanceOf(AvroDeclarationMismatchException::class.java)

  }

  @Test
  fun `package convention mismatch`() {
    val namespace = "com.acme.test"
    val name = "Foo"

    val fqn = SchemaFqn(Namespace(namespace + ".foo"), Name(name))
    val schema = simpleStringValueSchema(Namespace(namespace), Name(name))

    val declaration = SchemaDeclaration(fqn, schema)

    assertThat(declaration.contentFqn.namespace.value).isEqualTo(namespace)
    assertThat(declaration.contentFqn.name.value).isEqualTo(name)
    assertThat(declaration.contentFqn.fileExtension).isEqualTo(AvroKotlin.Constants.EXTENSION_SCHEMA)

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
