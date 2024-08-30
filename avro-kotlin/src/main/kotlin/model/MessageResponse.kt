package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isArrayType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isNullable
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isUnionType
import java.util.function.Supplier

/**
 * Marks the response of a [org.apache.avro.Protocol.Message].
 */
sealed interface MessageResponse : Supplier<AvroSchema> {
  companion object {
    fun of(schema: AvroSchema): MessageResponse = if (EmptyType.schema == schema) {
      NONE
    } else if (schema.isArrayType) {
      MULTIPLE(schema)
    } else if (schema.isUnionType && schema.isNullable) {
      OPTIONAL(schema)
    } else {
      SINGLE(schema)
    }
  }

  val schema: AvroSchema

  /**
   * Gets the effective schema for the given response type (elementType for Array, ...)
   */
  abstract override fun get(): AvroSchema

  /**
   * NONE - the message has no response, only valid for one-way messages.
   */
  data object NONE : MessageResponse {
    /**
     * The original schema as read from message.
     */
    override val schema: AvroSchema = EmptyType.schema
    override fun get(): AvroSchema = schema
  }

  /**
   * SINGLE - the message has single, non-null response.
   */
  data class SINGLE(override val schema: AvroSchema) : MessageResponse {
    override fun get(): AvroSchema = schema
  }

  /**
   * OPTIONAL - the message response schema is a nullable union.
   */
  data class OPTIONAL(override val schema: AvroSchema) : MessageResponse {
    override fun get(): AvroSchema = UnionType(schema).reduce()
  }

  /**
   * MULTIPLE - the message response is an array of schema.
   */
  data class MULTIPLE(override val schema: AvroSchema) : MessageResponse {
    override fun get(): AvroSchema = ArrayType(schema).elementType.schema
  }
}

