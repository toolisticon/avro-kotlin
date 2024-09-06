package io.toolisticon.kotlin.avro.generator.strategy.internal

/**
 * Marker interface for strategies that are supposed to be essential to each generation.
 *
 * These strategies are not loaded via spi but referenced directly when needed.
 *
 * They are still customizable through registered [io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationProcessor]s.
 *
 * All implementations must reside in this package and modified with `internal`.
 */
internal sealed interface InternalStrategy
