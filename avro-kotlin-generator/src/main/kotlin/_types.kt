package io.toolisticon.kotlin.avro.generator

import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationContextFactory
import kotlin.reflect.KClass

/**
 * Declares a non-generic alias for the underlying avro4k/kotlinx serializer.
 */
typealias Avro4kSerializerKClass = KClass<out AvroSerializer<*>>

/**
 * Function to create the context from protocol.
 */
typealias ProtocolDeclarationContextFactory = KotlinCodeGenerationContextFactory<ProtocolDeclarationContext, ProtocolDeclaration>

/**
 * Function to create the context from schema.
 */
typealias SchemaDeclarationContextFactory = KotlinCodeGenerationContextFactory<SchemaDeclarationContext, SchemaDeclaration>
