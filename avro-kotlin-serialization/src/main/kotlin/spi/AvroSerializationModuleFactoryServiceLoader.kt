package io.toolisticon.kotlin.avro.serialization.spi

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import java.util.*

data object AvroSerializationModuleFactoryServiceLoader : AvroSerializerModuleFactory {

  private val modules = ServiceLoader.load(AvroSerializerModuleFactory::class.java)
    .fold(EmptySerializersModule()) { acc, cur ->
      acc + cur()
    }

  override fun invoke(): SerializersModule = modules
}
