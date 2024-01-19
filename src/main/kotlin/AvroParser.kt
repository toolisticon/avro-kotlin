package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.declaration.AvroDeclaration
import io.toolisticon.avro.kotlin.declaration.ProtocolDeclaration
import io.toolisticon.avro.kotlin.declaration.SchemaDeclaration
import io.toolisticon.avro.kotlin.model.AvroSource
import io.toolisticon.avro.kotlin.model.FileSource
import io.toolisticon.avro.kotlin.model.JsonSource
import io.toolisticon.avro.kotlin.model.ResourceSource
import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.JsonString
import mu.KLogging
import org.apache.avro.LogicalTypes
import org.apache.avro.LogicalTypes.LogicalTypeFactory
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.File
import java.net.URL

/**
 * Parsing of json files is internally still done via the avro java core implementation ([Schema.Parser.parse] and [Protocol.parse]),
 * but this parser analyses and enriches all schema with metadata and returns an [AvroDeclaration].
 *
 */
class AvroParser(
  val verifyPackageConvention: Boolean = true,
) {
  companion object : KLogging()

  /**
   * Adds custom [LogicalTypeFactory] to static avro context.
   *
   * @see [LogicalTypes#register] - this modifies a static context (sic!) and thus is not side effect free.
   *
   * @return self - for fluent usage
   */
  fun registerLogicalTypeFactory(vararg logicalTypeFactories: LogicalTypeFactory) = apply {
    logicalTypeFactories.forEach { LogicalTypes.register(it) }
  }

  private fun schemaDeclaration(schema: AvroSchema, source: AvroSource): SchemaDeclaration {
    requireNotNull(schema.namespace) { "A top level schema declaration must have a namespace." }
    requireNotNull(schema.name) { "A schema must have a name." }
    // FIXME: validate packageConvention here!
    return SchemaDeclaration(schema, source)
  }

  private fun protocolDeclaration(protocol: AvroProtocol, source: AvroSource): ProtocolDeclaration {
    // FIXME: validate packageConvention here!
    return ProtocolDeclaration(protocol = protocol, source = source)
  }

  /**
   * @param resource - URL of avsc to parse
   * @return the [SchemaDeclaration] containing parse result
   */
  fun parseSchema(resource: URL): SchemaDeclaration {
    val schema = AvroSchema(resource)
    val source = ResourceSource(schema.json, AvroSpecification.SCHEMA, resource)

    return schemaDeclaration(schema, source)
  }

  /**
   * @param file - avsc File to parse
   * @return the [SchemaDeclaration] containing parse result
   */
  fun parseSchema(file: File): SchemaDeclaration {
    val schema = AvroKotlin.parseSchema(file)
    val source = FileSource(schema.json, AvroSpecification.SCHEMA, file)

    return schemaDeclaration(schema, source)
  }

  /**
   * @param schema the schema
   * @return [SchemaDeclaration] wrapping the schema.
   */
  fun parseSchema(schema: Schema) = parseSchema(JsonString(schema))

  /**
   * @param json - the json string of the [Schema] to parse
   * @return the [SchemaDeclaration] containing parse result
   */
  fun parseSchema(json: JsonString): SchemaDeclaration = schemaDeclaration(
    schema = AvroSchema(json, true),
    source = JsonSource(json, AvroSpecification.SCHEMA)
  )


  /**
   * @param resource - URL of avpr to parse
   * @return the [ProtocolDeclaration] containing parse result
   */
  fun parseProtocol(resource: URL): ProtocolDeclaration {
    val protocol = AvroKotlin.parseProtocol(resource)
    val json = JsonString(protocol.toString(pretty = true))

    return protocolDeclaration(protocol, ResourceSource(json, AvroSpecification.PROTOCOL, resource))
  }

  /**
   * @param file - avpr File to parse
   * @return the [ProtocolDeclaration] containing parse result
   */
  fun parseProtocol(file: File): ProtocolDeclaration {
    val protocol = AvroKotlin.parseProtocol(file)
    val source = FileSource(json = protocol.json, specification = AvroSpecification.PROTOCOL, file = file)

    return protocolDeclaration(
      protocol = protocol,
      source = source
    )
  }

  /**
   * @param json - the json string of the [Protocol] to parse
   * @return the [ProtocolDeclaration] containing parse result
   */
  fun parseProtocol(json: JsonString): ProtocolDeclaration {
    val protocol = AvroKotlin.parseProtocol(json)
    val source = JsonSource(json = json, specification = AvroSpecification.PROTOCOL)

    return protocolDeclaration(protocol = protocol, source = source)
  }
}
