package io.toolisticon.kotlin.avro.generator.test

import io.toolisticon.avro.avro4k.schema.SimpleTypesRecord
import io.toolisticon.avro.avro4k.schema.SimpleTypesRecordData
import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import org.apache.avro.message.SchemaStore
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SimpleTypesRecordITest {

  @Test
  fun `schema of java and kotlin are identical`() {
    val fingerprintJava = AvroFingerprint.of(SimpleTypesRecord.getClassSchema())
    val fingerprintKotlin = AvroKotlinSerialization().schema(SimpleTypesRecordData::class).fingerprint

    assertThat(fingerprintKotlin).isEqualTo(fingerprintJava)
  }

  @Test
  fun `can write java to json and read kotlin`() {
    val store = SchemaStore.Cache().apply {
      addSchema(SimpleTypesRecord.getClassSchema())
    }

    val msg = SimpleTypesRecord.newBuilder()
      .setBooleanValue(true)
      .setDoubleValue(1.0)
      .setIntValue(5)
      .setFloatValue(1.8F)
      .setStringValue("foo")
      .build()

    val javaBytes = SingleObjectEncodedBytes.of(SimpleTypesRecord.getEncoder().encode(msg))

    val avro = AvroKotlinSerialization()

    val schemaResolver = avroSchemaResolver(AvroSchema(SimpleTypesRecord.getClassSchema()))


    val kotlinInstance: SimpleTypesRecordData = avro.decodeFromSingleObjectEncoded(javaBytes)

    assertThat(kotlinInstance.booleanValue).isEqualTo(msg.booleanValue)
    assertThat(kotlinInstance.doubleValue).isEqualTo(msg.doubleValue)
    assertThat(kotlinInstance.intValue).isEqualTo(msg.intValue)
    assertThat(kotlinInstance.floatValue).isEqualTo(msg.floatValue)
    assertThat(kotlinInstance.stringValue).isEqualTo(msg.stringValue)
  }
}
