package io.toolisticon.avro.kotlin.example.money

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import com.github.avrokotlin.avro4k.schema.AvroDescriptor
import com.github.avrokotlin.avro4k.schema.NamingStrategy
import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.kotlin.avro.serialization.kind
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import org.apache.avro.Schema
import org.javamoney.moneta.Money

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Money::class)
class MoneySerializer : AvroSerializer<Money>() {

  private val conversion = MoneyLogicalType.MoneyConversion()
  private val logicalType = MoneyLogicalType
  private val schemaType = SchemaType.STRING

  override val descriptor: SerialDescriptor = object :AvroDescriptor(Money::class, schemaType.kind) {
    override fun schema(annos: List<Annotation>, serializersModule: SerializersModule, namingStrategy: NamingStrategy): Schema {
      return logicalType.addToSchema(schemaType.schema().get())
    }
  }

  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): Money {
    require(schema.logicalType is MoneyLogicalType) { "schema does not  have logical type: ${MoneyLogicalType.name}."}
    logicalType.validate(schema)

    return conversion.fromAvro(decoder.decodeString())
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: Money) {
    logicalType.validate(schema)

    encoder.encodeString(conversion.toAvro(obj))
  }


}
