package io.toolisticon.kotlin.avro.generator

import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.context.AvroKotlinGeneratorContextFactory
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinObjectSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import mu.KLogging


class AvroKotlinGenerator(
  private val contextFactory: AvroKotlinGeneratorContextFactory
) {
  companion object : KLogging() {
  }

  constructor(properties: AvroKotlinGeneratorProperties = AvroKotlinGeneratorProperties()) : this(
    contextFactory = AvroKotlinGeneratorContextFactory(properties = properties)
  )

  fun generate(declaration: SchemaDeclaration): KotlinFileSpec {
    val ctx = contextFactory.create(declaration)
    val recordType = declaration.recordType

    val fileSpecBuilder = KotlinFileSpecBuilder.builder(ctx.rootClassName)

    val dataClass = ctx.strategies.generateDataClassStrategy.generateDataClass(ctx, recordType)

    ctx.processors.fileSpecProcessors(ctx, ctx.rootClassName, fileSpecBuilder)

    return fileSpecBuilder.addType(dataClass).build()
  }

  fun generate(declaration: ProtocolDeclaration): KotlinFileSpec {
    val ctx = contextFactory.create(declaration)

    val fileSpecBuilder = KotlinFileSpecBuilder.builder(ctx.rootClassName)

    val dataClasses = declaration.protocol.recordTypes.filterIsInstance<RecordType>().map {
      ctx.strategies.generateDataClassStrategy.generateDataClass(ctx, it)
    }

    val o = KotlinObjectSpecBuilder.builder(ctx.rootClassName)
      .builder {
        addTypes(dataClasses.map { it.get() })
      }
      .build()

    return fileSpecBuilder.addType(o).build()
  }
}
