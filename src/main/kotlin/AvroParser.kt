package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.declaration.AvroDeclaration
import io.toolisticon.avro.kotlin.declaration.ProtocolDeclaration
import io.toolisticon.avro.kotlin.declaration.SchemaDeclaration
import io.toolisticon.avro.kotlin.ktx.documentation
import io.toolisticon.avro.kotlin.model.*
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import mu.KLogging
import org.apache.avro.LogicalTypes
import org.apache.avro.LogicalTypes.LogicalTypeFactory
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.File
import java.net.URL
import java.util.concurrent.Callable

/**
 * Parsing of json files is internally still done via the avro java core implementation ([Schema.Parser.parse] and [Protocol.parse]),
 * but this parser analyses and enriches all schema with metadata and returns an [AvroDeclaration].
 *
 */
class AvroParser(
  val verifyPackageConvention: Boolean = true,
  // validation setters on Schema.Parser .. make configurable?
  //private boolean validate = true;
  //    private boolean validateDefaults = true;
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

  /**
   * @param resource - URL of avsc to parse
   * @return the [SchemaDeclaration] containing parse result
   */
  fun parseSchema(resource: URL): SchemaDeclaration {
    val schema = AvroKotlin.parseSchema(resource)
    val source = ResourceSource(schema.json, AvroSpecification.SCHEMA, resource)


    return ProcessSchema(
      schema = schema,
      source = source
    ).call()
  }

  /**
   * @param file - avsc File to parse
   * @return the [SchemaDeclaration] containing parse result
   */
  fun parseSchema(file: File): SchemaDeclaration {
    val schema = AvroKotlin.parseSchema(file)
    val source = FileSource(schema.json, AvroSpecification.SCHEMA, file)

    return ProcessSchema(
      schema = schema,
      source = source
    ).call()
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
  fun parseSchema(json: JsonString): SchemaDeclaration = ProcessSchema(
    schema = AvroKotlin.parseSchema(json, true),
    source = JsonSource(json, AvroSpecification.SCHEMA)
  ).call()


  /**
   * @param resource - URL of avpr to parse
   * @return the [ProtocolDeclaration] containing parse result
   */
  fun parseProtocol(resource: URL): ProtocolDeclaration {
    TODO()
//    val schema = AvroSchema(schema = file.parseSchema(), isRoot = true)
//    val source = AvroKotlin.AvroSource.FileSource(schema.json, AvroKotlin.Specification.SCHEMA, file)
//
//    return  ProcessSchema(
//      schema = schema,
//      source = source
//    ).call()
//  = ProcessProtocol(protocol = resource.parseProtocol(), source = ResourceSource(resource)).call()
  }

  /**
   * @param file - avpr File to parse
   * @return the [ProtocolDeclaration] containing parse result
   */
  fun parseProtocol(file: File): ProtocolDeclaration {
    TODO()
//    val schema = AvroSchema(schema = file.parseSchema(), isRoot = true)
//    val source = AvroKotlin.AvroSource.FileSource(schema.json, AvroKotlin.Specification.SCHEMA, file)
//
//    return  ProcessSchema(
//      schema = schema,
//      source = source
//    ).call()
//  ProcessProtocol(protocol = file.parseProtocol(), source = FileSource(file)).call()
  }

  /**
   * @param json - the json string of the [Protocol] to parse
   * @return the [ProtocolDeclaration] containing parse result
   */
  fun parseProtocol(json: JsonString): ProtocolDeclaration {
    TODO()
//    val schema = AvroSchema(schema = file.parseSchema(), isRoot = true)
//    val source = AvroKotlin.AvroSource.FileSource(schema.json, AvroKotlin.Specification.SCHEMA, file)
//
//    return  ProcessSchema(
//      schema = schema,
//      source = source
//    ).call()
//  ProcessProtocol(protocol = json.parseProtocol(), source = JsonSource).call()
  }

  /**
   * Common super class of [ProcessSchema] and [ProcessProtocol].
   */
  private abstract inner class ProcessAvro<T : AvroDeclaration>(
    val namespace: Namespace,
    val name: Name,
    val json: JsonString,
    val source: AvroSource,
  ) : Callable<T>

  /**
   * Internal stateful processor for a given [Schema].
   */
  private inner class ProcessSchema(
    val schema: AvroSchema,
    source: AvroSource
  ) : ProcessAvro<SchemaDeclaration>(
    namespace = requireNotNull(schema.namespace) { "A top level schema declaration must have a namespace." },
    name = requireNotNull(schema.name) { "A schema must have a name." },
    json = schema.json,
    source = source
  ) {

    override fun call(): SchemaDeclaration = SchemaDeclaration(
      originalJson = json,
      source = source,
      schema = schema.get(),
      namespace = namespace,
      name = name,
      documentation = schema.documentation,
      recordType = schema.recordType(),
      avroTypes = AvroTypesMap(schema),
    )
  }

  /**
   * Internal stateful parser for a [Protocol].
   */
  private inner class ProcessProtocol(val protocol: Protocol, source: AvroSource) : ProcessAvro<ProtocolDeclaration>(
    namespace = Namespace(requireNotNull(protocol.namespace) { "A protocol must have a namespace" }),
    name = with(protocol.name) {
      require(this != null && !this.contains(".")) { "A protocol name must be a simpleName, not an FQN (basically: contain no dots)." }
      Name(this)
    },
    json = JsonString(protocol),
    source = source
  ) {

    override fun call(): ProtocolDeclaration {
      return ProtocolDeclaration(
        originalJson = json,
        namespace = Namespace(protocol.namespace),
        name = Name(protocol.name),
        documentation = protocol.documentation,
        protocol = protocol,
      )
    }
  }
}
