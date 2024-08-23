package io.toolisticon.kotlin.avro.generator.logical

import io.toolisticon.kotlin.avro.generator.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class UuidLogicalTypeDefinitionTest {

  @Test
  fun `add contextual to logical type`() {
    val declaration = TestFixtures.parseDeclaration("schema/SimpleUUIDRecord.avsc")

    assertThat(TestFixtures.DEFAULT_GENERATOR.registry.logicalTypes).isNotEmpty()
    assertThat(TestFixtures.DEFAULT_GENERATOR.registry.logicalTypes).isNotEmpty()

    val file = TestFixtures.DEFAULT_GENERATOR.generate(declaration)

    println(file.code)

  }
}
