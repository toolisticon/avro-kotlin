package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.value.AvroSpecification.SCHEMA
import org.assertj.core.api.Assertions.assertThat
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
  fun `canonicalName toString`(namespace: String?, name: String, expected: String) {
    val ns = namespace?.let { Namespace(it) } ?: Namespace.EMPTY
    val cn = CanonicalName(namespace = ns, name = Name(name))

    assertThat(cn).hasToString(expected)
  }
}
