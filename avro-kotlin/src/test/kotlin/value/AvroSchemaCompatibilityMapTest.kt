package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.TestFixtures.DummyEvents
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroSchemaCompatibilityMapTest {

  @Test
  fun `schemas are incompatible`() {
    val cache = AvroSchemaCompatibilityMap()

    val result = cache.compatibleToReadFrom(DummyEvents.SCHEMA_EVENT_01, DummyEvents.SCHEMA_EVENT_10)

    assertThat(result.isCompatible).isFalse()

    // cached result
    assertThat(cache.isCompatible(DummyEvents.SCHEMA_EVENT_01.fingerprint, DummyEvents.SCHEMA_EVENT_10.fingerprint)).isFalse()
  }
}
