package io.toolisticon.kotlin.avro.generator.api.context

import io.toolisticon.kotlin.generation.spi.AbstractKotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry

class TestContext(registry: KotlinCodeGenerationSpiRegistry<TestContext>) : AbstractKotlinCodeGenerationContext<TestContext>(registry) {
}
