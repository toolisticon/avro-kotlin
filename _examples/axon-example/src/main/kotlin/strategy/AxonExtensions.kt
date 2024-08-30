package io.holixon.axon.avro.generator.strategy

import io.holixon.axon.avro.generator.meta.RecordMetaData.Companion.recordMetaData
import io.holixon.axon.avro.generator.meta.RecordMetaDataType
import io.toolisticon.kotlin.avro.model.AvroType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol

fun AvroProtocol.Message.isQuery(): Boolean {
  return this.request.fields
    .map { AvroType.avroType<AvroType>(it.schema) }
    .filterIsInstance<RecordType>()
    .mapNotNull { it.recordMetaData() }
    .any { RecordMetaDataType.Query == it.type }
  // TODO: analyze response of query
  // RecordMetaDataType.QueryResult == it.type
}
