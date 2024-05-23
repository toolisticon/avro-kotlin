package io.toolisticon.avro.kotlin.example.customerid

import io.toolisticon.avro.kotlin.logical.StringLogicalType
import io.toolisticon.avro.kotlin.logical.StringLogicalTypeFactory
import io.toolisticon.avro.kotlin.logical.conversion.StringLogicalTypeConversion
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName
import io.toolisticon.kotlin.avro.serialization.serializer.StringLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import kotlinx.serialization.modules.SerializersModule

object CustomerIdLogicalType : StringLogicalType(name = "customer-id".toLogicalTypeName()) {

  class CustomerIdLogicalTypeFactory : StringLogicalTypeFactory<CustomerIdLogicalType>(logicalType = CustomerIdLogicalType)

  class CustomerIdConversion : StringLogicalTypeConversion<CustomerIdLogicalType,CustomerId>(CustomerIdLogicalType, CustomerId::class) {
    override fun fromAvro(value: String): CustomerId = CustomerId.of(value)
    override fun toAvro(value: CustomerId): String = value.id
  }

  class CustomerIdSerializer : StringLogicalTypeSerializer<CustomerIdLogicalType,CustomerId>(CustomerIdConversion())

  class CustomerIdSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(CustomerId::class, CustomerIdSerializer())
    }
  }
}
