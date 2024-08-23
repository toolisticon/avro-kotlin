package io.toolisticon.kotlin.avro.generator.test

import io.toolisticon.avro.avro4k.schema.SimpleUUIDRecord
import io.toolisticon.avro.avro4k.schema.SimpleUUIDRecordData
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SimpleUUIDRecordITest {

  private val avro = AvroKotlinSerialization()

  @Test
  fun `schema of java and kotlin are identical`() {
    val fingerprintJava = AvroFingerprint.of(SimpleUUIDRecord.getClassSchema())
    val fingerprintKotlin = avro.schema(SimpleUUIDRecordData::class).fingerprint

    assertThat(fingerprintKotlin).isEqualTo(fingerprintJava)
  }
}
