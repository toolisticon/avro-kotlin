package io.toolisticon.kotlin.avro.serialization.spi

import io.toolisticon.kotlin.avro.serialization.spi.SerializerModuleKtx.reduce
import kotlinx.serialization.modules.SerializersModule
import java.util.*

data object AvroSerializationModuleFactoryServiceLoader : AvroSerializerModuleFactory {
  private val modules = ServiceLoader.load(AvroSerializerModuleFactory::class.java)
    .map { it.invoke() }
    .reduce()

  override fun invoke(): SerializersModule = modules
}
