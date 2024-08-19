package io.toolisticon.kotlin.avro.generator

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace


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
