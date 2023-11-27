package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.ktx.fqnToPath
import io.toolisticon.avro.kotlin.value.AvroSpecification.SCHEMA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CanonicalNameTest {

  @Test
  fun `fqn to path`() {
    val cn = CanonicalName(namespace = Namespace("com.acme.ns"), name = Name("Foo"))

    val path = cn.toPath(SCHEMA)

    assertThat(path.toString()).isEqualTo("com/acme/ns/Foo.avsc")
  }

  @Test
  fun `cn to fqn`() {
    val cn = Namespace("com.acme.test") + Name( "HelloWorld")
    assertThat(cn.toString()).isEqualTo("com.acme.test.HelloWorld")
  }

}
