package io.toolisticon.lib.avro.spike

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.AvroName
import com.github.avrokotlin.avro4k.AvroNamespace
import kotlin.String
import kotlin.jvm.JvmField
import kotlinx.serialization.Serializable
import org.apache.avro.specific.AvroGenerated
import org.junit.jupiter.api.Test

/**
 * This is a record with a simple string value.
 */
@AvroGenerated
@Serializable
@AvroNamespace("io.acme.schema")
@AvroName("SimpleStringRecord")
data class SimpleStringRecord(
  @JvmField val value: String,
)

internal class XTest {

  @Test
  fun name() {
    println(Avro.default.schema(SimpleStringRecord.serializer()))
  }
}
