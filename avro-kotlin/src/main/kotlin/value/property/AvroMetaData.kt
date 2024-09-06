package io.toolisticon.kotlin.avro.value.property

import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema

/**
 * Marker interface for type derived via [AvroMetaDataProperty].
 */
interface AvroMetaData


/**
 * Extract typed Meta properties.
 */
inline fun <reified META : AvroMetaData> AvroSchema.metaData(extractor: AvroSchema.() -> META?): META? = this.extractor()

/**
 * Extract typed Meta properties.
 */
inline fun <reified META : AvroMetaData> AvroProtocol.metaData(extractor: AvroProtocol.() -> META?): META? = this.extractor()
