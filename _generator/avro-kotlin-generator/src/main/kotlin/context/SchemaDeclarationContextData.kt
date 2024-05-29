package io.toolisticon.kotlin.avro.generator.context

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.api.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.processor.AvroKotlinGeneratorProcessors
import io.toolisticon.kotlin.avro.generator.api.strategy.AvroKotlinGeneratorStrategies

data class SchemaDeclarationContextData(
  override val rootClassName: ClassName,
  override val isRoot: Boolean = true,
  override val declaration: SchemaDeclaration,
  override val properties: AvroKotlinGeneratorProperties,
  override val avroPoetTypes: AvroPoetTypes,
  override val processors: AvroKotlinGeneratorProcessors,
  override val strategies: AvroKotlinGeneratorStrategies
) : SchemaDeclarationContext {


}
