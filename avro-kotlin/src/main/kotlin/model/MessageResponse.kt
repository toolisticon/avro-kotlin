package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isArrayType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isNullable
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isUnionType

/**
 * Marks the response of a [org.apache.avro.Protocol.Message].
 */
sealed interface MessageResponse {
  companion object {
    fun of(schema: AvroSchema): MessageResponse = if(EmptyType.schema == schema) {
      NONE
    } else if (schema.isArrayType) {
      MULTIPLE(ArrayType(schema).elementType.schema)
    } else if (schema.isUnionType && schema.isNullable) {
      OPTIONAL(UnionType(schema).reduce())
    } else {
      SINGLE(schema)
    }
  }
  val schema: AvroSchema

  /**
   * NONE - the message has no response, only valid for one-way messages.
   */
  data object NONE : MessageResponse {
    override val schema: AvroSchema = EmptyType.schema
  }

  /**
   * SINGLE - the message has single, non-null response.
   */
  data class SINGLE(override val schema: AvroSchema) : MessageResponse

  /**
   * OPTIONAL - the message response schema is a nullable union.
   */
  data class OPTIONAL(override val schema: AvroSchema) : MessageResponse

  /**
   * MULTIPLE - the message response is an array of schema.
   */
  data class MULTIPLE(override val schema: AvroSchema) : MessageResponse
}

