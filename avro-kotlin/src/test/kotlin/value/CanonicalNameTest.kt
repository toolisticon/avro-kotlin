package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.value.AvroSpecification.SCHEMA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CanonicalNameTest {

  @ParameterizedTest
  @CsvSource(
    value = [
      "com.acme.ns, Foo, com/acme/ns/Foo.avsc",
      ", Foo, Foo.avsc",
    ]
  )
  @DisabledOnOs(OS.WINDOWS)
  fun `canonicalName toPath`(namespace: String?, name: String, expected: String) {
    val ns = namespace?.let { Namespace(it) } ?: Namespace.EMPTY
    val cn = CanonicalName(namespace = ns, name = Name(name))

    val path = cn.toPath(SCHEMA)

    assertThat(path).hasToString(expected)
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "com.acme.ns, Foo, com.acme.ns.Foo",
      ", Foo, Foo",
      "com.acme.test, HelloWorld, com.acme.test.HelloWorld",
    ]
  )
  @DisabledOnOs(OS.WINDOWS)
  fun `canonicalName fqn`(namespace: String?, name: String, expected: String) {
    val ns = namespace?.let { Namespace(it) } ?: Namespace.EMPTY
    val cn = CanonicalName(namespace = ns, name = Name(name))

    assertThat(cn.fqn).isEqualTo(expected)
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "bar,Foo,CanonicalName(fqn='bar.Foo')",
      "null,Foo,CanonicalName(fqn='Foo')",
    ], nullValues = ["null"]
  )
  fun `verify toString`(namespaceParameter: String?, nameParameter: String?, expected: String) {
    val namespace = namespaceParameter?.let { Namespace(it) } ?: Namespace.EMPTY
    val name = nameParameter?.let { Name(nameParameter) } ?: Name.EMPTY
    val canonicalName = namespace + name

    assertThat(canonicalName.toString()).isEqualTo(expected)
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "null",
      "Foo",
      "bar.Foo",
    ], nullValues = ["null"]
  )
  fun `construct from fqn string`(fqnParam: String?) {
    val fqn = fqnParam ?: ""
    val canonicalName = CanonicalName.parse(fqn)
    assertThat(canonicalName.fqn).isEqualTo(fqn)
  }
}
