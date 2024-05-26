package io.toolisticon.kotlin.avro.serialization.spi

import kotlinx.serialization.modules.SerializersModule
import java.util.*

/**
 * Used to register custom [com.github.avrokotlin.avro4k.serializer.AvroSerializer] via [ServiceLoader].
 */
interface AvroSerializerModuleFactory : () -> SerializersModule
