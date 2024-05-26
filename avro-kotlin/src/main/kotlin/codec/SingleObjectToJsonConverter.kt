package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import org.apache.avro.generic.GenericData
import org.apache.avro.message.MissingSchemaException

class SingleObjectToJsonConverter(
    private val avroSchemaResolver: AvroSchemaResolver,
    private val genericData: GenericData
) : AvroCodec.Converter<SingleObjectEncodedBytes, JsonString> {

  @Throws(MissingSchemaException::class)
  override fun convert(source: SingleObjectEncodedBytes): JsonString {
    val schema = avroSchemaResolver[source.fingerprint]
    val record = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = source,
      readerSchema = schema,
      genericData = genericData
    )
    return GenericRecordCodec.encodeJson(record, genericData)
  }
}

