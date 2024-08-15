package io.toolisticon.kotlin.avro.generator

import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import kotlin.reflect.KClass

/**
 * Declares a non-generic alias for the underlying avro4k/kotlinx serializer.
 */
typealias Avro4kSerializerKClass = KClass<out AvroSerializer<*>>
