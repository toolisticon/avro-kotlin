package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.spi.strategy.DataClassSpecStrategy

@OptIn(ExperimentalKotlinPoetApi::class)
abstract class AbstractDataClassFromRecordTypeStrategy :
  DataClassSpecStrategy<SchemaDeclarationContext, RecordType>(
    contextType = SchemaDeclarationContext::class, inputType = RecordType::class
  )
