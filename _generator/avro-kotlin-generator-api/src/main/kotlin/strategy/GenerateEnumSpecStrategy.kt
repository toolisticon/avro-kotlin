package io.toolisticon.kotlin.avro.generator.api.strategy

import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec

interface GenerateEnumClassStrategy : AvroKotlinGeneratorStrategy {

  fun generateEnumClass(
    ctx: AvroDeclarationContext,
    enumType: EnumType
  ) : KotlinEnumClassSpec

}

abstract class AbstractGenerateEnumClassStrategy(
  override val order: Int = AvroKotlinGeneratorSpi.DEFAULT_ORDER
) : GenerateEnumClassStrategy
