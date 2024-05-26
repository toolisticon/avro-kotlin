package io.toolisticon.kotlin.avro.example.customerid

import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.logical.StringLogicalTypeFactory
import io.toolisticon.kotlin.avro.logical.conversion.StringLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.serializer.StringLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import io.toolisticon.kotlin.avro.value.LogicalTypeName.Companion.toLogicalTypeName
import kotlinx.serialization.modules.SerializersModule

object CustomerIdDataLogicalType : StringLogicalType(name = "customer-id-data".toLogicalTypeName()) {

  class CustomerIdDataLogicalTypeFactory : StringLogicalTypeFactory<CustomerIdDataLogicalType>(logicalType = CustomerIdDataLogicalType)

  class CustomerIdDataConversion : StringLogicalTypeConversion<CustomerIdDataLogicalType, CustomerIdData>(CustomerIdDataLogicalType, CustomerIdData::class) {
    override fun fromAvro(value: String): CustomerIdData = CustomerIdData(value)
    override fun toAvro(value: CustomerIdData): String = value.id
  }

  class CustomerIdDataSerializer : StringLogicalTypeSerializer<CustomerIdDataLogicalType, CustomerIdData>(CustomerIdDataConversion())


  class CustomerIdDataSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(CustomerIdData::class, CustomerIdDataSerializer())
    }
  }
}
