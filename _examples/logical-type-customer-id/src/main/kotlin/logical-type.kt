package io.toolisticon.avro.kotlin.example.customerid

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import com.github.avrokotlin.avro4k.schema.AvroDescriptor
import com.github.avrokotlin.avro4k.schema.NamingStrategy
import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import io.toolisticon.avro.kotlin.logical.conversion.StringConversion
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import io.toolisticon.kotlin.avro.serialization.kind
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes.LogicalTypeFactory
import org.apache.avro.Schema

val LOGICAL_TYPE_NAME = LogicalTypeName("customer-id")

class CustomerIdLogicalType : LogicalType(LOGICAL_TYPE_NAME.value)

class CustomerIdConversion : StringConversion<CustomerId>(
  logicalTypeName = LOGICAL_TYPE_NAME,
  convertedType = CustomerId::class.java
) {
  override fun fromAvro(value: String, schema: AvroSchema, logicalType: LogicalType?): CustomerId {
    return CustomerId(value)
  }

  override fun toAvro(value: CustomerId, schema: AvroSchema, logicalType: LogicalType?): String {
    return value.id
  }
}

class CustomerIdLogicalTypeFactory : LogicalTypeFactory {

  override fun fromSchema(schema: Schema): LogicalType {
    return CustomerIdLogicalType()
  }

  override fun getTypeName(): String = LOGICAL_TYPE_NAME.value
}


@OptIn(ExperimentalSerializationApi::class)
class CustomerIdSerializer : AvroSerializer<CustomerId>() {

  private val logicalType = CustomerIdLogicalType()
  private val conversion = CustomerIdConversion()

  override val descriptor: SerialDescriptor = object : AvroDescriptor(type = CustomerId::class, kind = SchemaType.STRING.kind) {
    override fun schema(annos: List<Annotation>, serializersModule: SerializersModule, namingStrategy: NamingStrategy): Schema {
      return logicalType.addToSchema(SchemaType.STRING.schema().get())
    }

  }

  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CustomerId {
    return conversion.fromAvro(decoder.decodeString())
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CustomerId) {
    encoder.encodeString(conversion.toAvro(obj))
  }

}

class CustomerIdSerializerModuleFactory : AvroSerializerModuleFactory {
  override fun invoke(): SerializersModule = SerializersModule {
    contextual(CustomerId::class, CustomerIdSerializer())
  }
}
