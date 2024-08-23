package io.toolisticon.kotlin.avro.generator.test

import com.github.avrokotlin.avro4k.Avro
import io.toolisticon.avro.avro4k.schema.SimpleTypesRecord
import io.toolisticon.avro.avro4k.schema.SimpleTypesRecordData
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolverMutableMap
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SimpleTypesRecordITest {
  private val schemaResolver = AvroSchemaResolverMutableMap(SimpleTypesRecord.getClassSchema())
  private val avro = AvroKotlinSerialization(avro4k = Avro.Default, schemaResolver = schemaResolver)

  @Test
  fun `schema of java and kotlin are identical`() {
    val fingerprintJava = AvroFingerprint.of(SimpleTypesRecord.getClassSchema())
    val fingerprintKotlin = avro.schema(SimpleTypesRecordData::class).fingerprint

    assertThat(fingerprintKotlin).isEqualTo(fingerprintJava)
  }

  @Test
  fun `can write java to json and read kotlin`() {
    val msg = SimpleTypesRecord.newBuilder()
      .setBooleanValue(true)
      .setDoubleValue(1.0)
      .setIntValue(5)
      .setFloatValue(1.8F)
      .setStringValue("foo")
      .build()

    val javaBytes = SingleObjectEncodedBytes.of(SimpleTypesRecord.getEncoder().encode(msg))


    val kotlinInstance: SimpleTypesRecordData = avro.decodeFromSingleObjectEncoded(javaBytes, SimpleTypesRecordData::class)

    assertThat(kotlinInstance.booleanValue).isEqualTo(msg.booleanValue)
    assertThat(kotlinInstance.doubleValue).isEqualTo(msg.doubleValue)
    assertThat(kotlinInstance.intValue).isEqualTo(msg.intValue)
    assertThat(kotlinInstance.floatValue).isEqualTo(msg.floatValue)
    assertThat(kotlinInstance.stringValue).isEqualTo(msg.stringValue)
  }
}
