

package io.toolisticon.kotlin.avro.serialization.avro4k

import com.github.avrokotlin.avro4k.Avro
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
