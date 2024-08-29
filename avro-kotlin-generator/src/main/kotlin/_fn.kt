@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.Documentation
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import io.toolisticon.kotlin.generation.builder.KotlinDocumentableBuilder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi
import io.toolisticon.kotlin.generation.spi.strategy.KotlinCodeGenerationStrategyBase
import io.toolisticon.kotlin.generation.spi.strategy.KotlinFileSpecStrategy
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import kotlin.reflect.KClass


fun rootClassName(avroDeclaration: AvroDeclaration, properties: AvroKotlinGeneratorProperties? = null) = avroClassName(
  namespace = avroDeclaration.namespace,
  name = avroDeclaration.name,
  properties = properties
)

fun avroClassName(namespace: Namespace, name: Name, properties: AvroKotlinGeneratorProperties? = null) = ClassName(
  packageName = namespace.value,
  simpleNames = listOf((name.suffix(properties?.schemaTypeSuffix)).value)
)

fun avroClassName(namedType: AvroNamedType, properties: AvroKotlinGeneratorProperties? = null) = avroClassName(
  namespace = namedType.namespace ?: Namespace(""),
  name = namedType.name,
  properties = properties
)

fun CanonicalName.asClassName() = ClassName(this.namespace.value, this.name.value)

fun KotlinDocumentableBuilder<*>.addKDoc(doc: Documentation?) = doc?.value?.let { this.addKdoc(it) }

