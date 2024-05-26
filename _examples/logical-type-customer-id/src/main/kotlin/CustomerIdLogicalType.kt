package io.toolisticon.kotlin.avro.example.customerid

import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.logical.StringLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.StringLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.StringLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object CustomerIdLogicalType : StringLogicalType(name = "customer-id".toLogicalTypeName()) {

  class CustomerIdLogicalTypeFactory : StringLogicalTypeFactory<CustomerIdLogicalType>(logicalType = CustomerIdLogicalType)

  class CustomerIdConversion : StringLogicalTypeConversion<CustomerIdLogicalType, CustomerId>(CustomerIdLogicalType, CustomerId::class) {
    override fun fromAvro(value: String): CustomerId = CustomerId.of(value)
    override fun toAvro(value: CustomerId): String = value.id
  }

  class CustomerIdSerializer : StringLogicalTypeSerializer<CustomerIdLogicalType, CustomerId>(CustomerIdConversion())

  class CustomerIdSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(CustomerId::class, CustomerIdSerializer())
    }
  }
}
