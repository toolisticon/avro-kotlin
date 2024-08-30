package io.holixon.axon.avro.generator.strategy

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import io.holixon.axon.avro.generator.meta.RecordMetaData.Companion.recordMetaData
import io.holixon.axon.avro.generator.meta.RecordMetaDataType
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.model.AvroType
import io.toolisticon.kotlin.avro.model.MessageResponse
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import java.util.*

fun AvroProtocol.Message.isQuery(): Boolean {
  return this.request.fields
    .map { AvroType.avroType<AvroType>(it.schema) }
    .filterIsInstance<RecordType>()
    .mapNotNull { it.recordMetaData() }
    .any { RecordMetaDataType.Query == it.type }
  // TODO: analyze response of query
  // RecordMetaDataType.QueryResult == it.type
}

/**
 * Resolves query response type.
 * TODO -> consider other collection types
 * @see https://docs.axoniq.io/reference-guide/axon-framework/queries/query-handlers
 */
fun AvroPoetTypes.resolveQueryResponseTypeName(messageResponse: MessageResponse): TypeName? =
  when(messageResponse) {
    is MessageResponse.NONE -> null
    is MessageResponse.SINGLE, is MessageResponse.MULTIPLE -> this[messageResponse.schema.hashCode].typeName
    is MessageResponse.OPTIONAL -> Optional::class.asTypeName().parameterizedBy(this[messageResponse.get().hashCode].typeName)
  }
