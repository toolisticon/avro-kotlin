package io.holixon.axon.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.holixon.axon.avro.generator.TestFixtures
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.generation.spi.strategy.KotlinCodeGenerationStrategyList
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotlinPoetApi::class)
class AxonEventHandlerInterfaceStrategyTest {

  private val declaration = TestFixtures.parseProtocol("BankAccountProtocol.avpr")
  private val strategy = AxonEventHandlerInterfaceStrategy()

  private val registry = AvroCodeGenerationSpiRegistry(
    strategies = KotlinCodeGenerationStrategyList(strategy)
  )

  private val context = ProtocolDeclarationContext.of(declaration, registry)

  @Test
  fun `create event handler interfaces`() {
    val file = strategy.invoke(context, declaration)

    println(file.code)
  }

}
