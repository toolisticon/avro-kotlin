package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroSchemaResolver
import io.toolisticon.avro.kotlin.codec.AvroCodec.Converter
import io.toolisticon.avro.kotlin.codec.AvroCodec.SingleObjectDecoder
import io.toolisticon.avro.kotlin.codec.AvroCodec.SingleObjectEncoder
import io.toolisticon.avro.kotlin.codec.SpecificRecordCodec.DecoderSpecificRecordClassResolver
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase
import org.apache.avro.util.ClassUtils

object SpecificRecordCodec {
  fun <T : SpecificRecordBase> avroSchema(recordType: Class<T>): AvroSchema = AvroSchema(schema = recordType.getDeclaredField("SCHEMA\$").get(null) as Schema)
  fun <T : SpecificRecordBase> specificData(recordType: Class<T>): SpecificData =
    recordType.getDeclaredField("MODEL\$").apply { isAccessible = true }.get(null) as SpecificData


  /**
   * Resolver for a concrete class used by decoding of Avro single object into Avro specific record.
   */
  fun interface DecoderSpecificRecordClassResolver : (AvroSchema) -> Class<SpecificRecordBase>

  /**
   * Default implementation using [Class.forName].
   */
  @Suppress("UNCHECKED_CAST")
  val reflectionBasedDecoderSpecificRecordClassResolver = DecoderSpecificRecordClassResolver {
    ClassUtils.forName(it.canonicalName.fqn) as Class<SpecificRecordBase>
  }

  @JvmStatic
  fun <T : SpecificRecordBase> encodeSingleObject(record: T): SingleObjectEncodedBytes {
    return specificRecordSingleObjectEncoder().encode(record)
  }

  @Suppress("UNCHECKED_CAST")
  @JvmStatic
  fun <T : SpecificRecordBase> decodeSingleObject(
    bytes: SingleObjectEncodedBytes,
    schemaResolver: AvroSchemaResolver
  ): T = specificRecordSingleObjectDecoder(schemaResolver).decode(bytes) as T

  /**
   * [Converter] from [SpecificRecordBase] to [GenericRecord].
   */
  @JvmStatic
  fun specificRecordToGenericRecordConverter() = Converter<SpecificRecordBase, GenericRecord> { specific ->
    val data = AvroCodec.genericData(specific.specificData)

    data.deepCopy(specific.schema, specific) as GenericRecord
  }

  /**
   * [Converter] from [GenericRecord] to [SpecificRecordBase].
   */
  @JvmStatic
  fun genericRecordToSpecificRecordConverter(readerType: Class<*>? = null) = Converter<GenericRecord, SpecificRecordBase> { generic ->
    val writerSchema = generic.schema

    // TODO if caller does not provide expected type, we use the writer schema to derive the class ... this could be wrong.
    val readerClass: Class<*> = AvroKotlin.specificData.getClass(writerSchema)

    val readerSpecificData = SpecificData.getForClass(readerClass)

    readerSpecificData.deepCopy(writerSchema, generic) as SpecificRecordBase
  }

  @JvmStatic
  fun specificRecordSingleObjectDecoder(writerSchemaResolver: AvroSchemaResolver): SingleObjectDecoder<SpecificRecordBase> =
    SpecificRecordSingleObjectDecoder(writerSchemaResolver)


  @JvmStatic
  fun specificRecordSingleObjectEncoder(): SingleObjectEncoder<SpecificRecordBase> = SpecificRecordSingleObjectEncoder()
}
