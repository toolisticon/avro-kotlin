package io.toolisticon.avro.kotlin.model.wrapper

import org.apache.avro.Schema
import java.util.function.Supplier

/**
 * Provides access to [Schema.Type].
 */
interface TypeSupplier : Supplier<Schema.Type>
