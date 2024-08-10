package io.toolisticon.kotlin.avro.serialization.avro4k

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.AvroDecoder
import kotlinx.serialization.descriptors.SerialDescriptor
import org.apache.avro.Schema
import org.apache.avro.util.WeakIdentityHashMap

@Suppress("ObjectPropertyName")
internal val _schemaCacheField = Avro::class.java.getDeclaredField("schemaCache").apply { isAccessible = true }

/**
 * Use reflection to inspect the internal avro4 schema cache,
 */
@Suppress("UNCHECKED_CAST")
internal fun Avro.schemaCache() = _schemaCacheField.get(this) as WeakIdentityHashMap<SerialDescriptor, Schema>

/**
 * We need to differentiate decoding for DIRECT and GENERIC.
 * As all implementations in avro4k are internal, we need this reflect workaround to derive a type.
 *
 * TODO: this should be part of avro4k, create an issue/start a discussion
 */
internal enum class AvroDecoderType {
  DIRECT, GENERIC;

  companion object {
    internal fun AvroDecoder.avroDecoderType(): AvroDecoderType = if ("RecordDirectDecoder" == this::class.simpleName) {
      DIRECT
    } else {
      GENERIC
    }
  }
}

