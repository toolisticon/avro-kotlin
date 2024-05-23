package io.toolisticon.avro.kotlin.example.customerid

import io.toolisticon.avro.kotlin.logical.StringLogicalType
import io.toolisticon.avro.kotlin.logical.StringLogicalTypeFactory
import io.toolisticon.avro.kotlin.logical.conversion.StringLogicalTypeConversion
import io.toolisticon.avro.kotlin.logical.conversion.TypeConverter
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName
import io.toolisticon.kotlin.avro.serialization.serializer.StringLogicalTypeSerializer
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import kotlinx.serialization.modules.SerializersModule

object CustomerIdDataLogicalType : StringLogicalType(name = "customer-id-data".toLogicalTypeName()) {

  class CustomerIdDataLogicalTypeFactory : StringLogicalTypeFactory<CustomerIdDataLogicalType>(logicalType = CustomerIdDataLogicalType)

  class CustomerIdDataConversion : StringLogicalTypeConversion<CustomerIdDataLogicalType,CustomerIdData>(CustomerIdDataLogicalType, CustomerIdData::class) {
    override fun fromAvro(value: String): CustomerIdData = CustomerIdData(value)
    override fun toAvro(value: CustomerIdData): String = value.id
  }

  class CustomerIdDataSerializer : StringLogicalTypeSerializer<CustomerIdDataLogicalType,CustomerIdData>(CustomerIdDataConversion())


  class CustomerIdDataSerializerModuleFactory : AvroSerializerModuleFactory {
    override fun invoke(): SerializersModule = SerializersModule {
      contextual(CustomerIdData::class, CustomerIdDataSerializer())
    }
  }
}
