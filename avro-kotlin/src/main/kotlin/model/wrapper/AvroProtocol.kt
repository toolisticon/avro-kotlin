package io.toolisticon.kotlin.avro.model.wrapper

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.avro.model.AvroTypesMap.Companion.typesMap
import io.toolisticon.kotlin.avro.value.*
import io.toolisticon.kotlin.avro.value.AvroFingerprint.Companion.sum
import org.apache.avro.Protocol
import java.util.function.Supplier

/**
 * A kotlin type- and null-safe wrapper around the java [Protocol].
 */
class AvroProtocol(
  private val protocol: Protocol
) : ProtocolSupplier, WithObjectProperties, WithDocumentation {
  companion object {
    /**
     * An error that can be thrown by any message.
     */
    val SCHEMA_SYSTEM_ERROR = AvroBuilder.primitiveSchema(SchemaType.STRING)

    /** Union type for generating system errors.  */
    val SCHEMA_SYSTEM_ERRORS = AvroBuilder.union(SCHEMA_SYSTEM_ERROR)

    val KEYWORDS: Set<String> = setOf("namespace", "protocol", "doc", "messages", "types", "errors")
    val MESSAGES: Set<String> = setOf("doc", "response", "request", "errors", "one-way")

    const val FILE_EXTENSION = "avpr"

  }

  /**
   * The namespace of this protocol. Qualifies its name.
   */
  override val namespace = Namespace(protocol.namespace)

  /**
   * The name of this protocol.
   */
  override val name = Name(protocol.name)

  override val canonicalName = CanonicalName(namespace, name)

  /**
   * Doc string for this protocol.
   */
  override val documentation = Documentation.ofNullable(protocol.doc)
  override val properties = ObjectProperties.ofNullable(protocol)

  /**
   * Extract typed Meta properties.
   */
  inline fun <reified META : Any> getMeta(extractor: AvroProtocol.() -> META?): META? = this.extractor()

  val md5: ByteArray by lazy { protocol.mD5 }

  /**
   * [Protocol] does not have a [AvroFingerprint]. To identify an identical
   * protocol, we can calculate the fingerprint using all types and messages.
   */
  val fingerprint: AvroFingerprint by lazy {
    buildList {
      add(AvroFingerprint.ofNullable(protocol.namespace))
      add(AvroFingerprint.ofNullable(protocol.name))
      addAll(protocol.types.map { AvroFingerprint.of(it) })
      addAll(protocol.messages.values.map { AvroFingerprint.of(it) })
    }.sum()
  }

  override val json: JsonString by lazy { JsonString.of(protocol) }

  override val hashCode = AvroHashCode.of(protocol)

  /**
   * A protocol message.
   *
   * A message has attributes:
   *
   * * a doc, an optional description of the message,
   * * a request, a list of named, typed parameter schemas (this has the same form as the fields of a record declaration);
   * * a response schema;
   * * an optional union of declared error schemas. The effective union has “string” prepended to the declared union, to permit transmission of undeclared “system” errors. For example, if the declared error union is ["AccessError"], then the effective union is ["string", "AccessError"]. When no errors are declared, the effective error union is ["string"]. Errors are serialized using the effective union; however, a protocol’s JSON declaration contains only the declared union.
   * * an optional one-way boolean parameter.
   * * A request parameter list is processed equivalently to an anonymous record. Since record field lists may vary between reader and writer, request parameters may also differ between the caller and responder, and such differences are resolved in the same manner as record field differences.
   *
   * The one-way parameter may only be true when the response type is "null" and no errors are listed.
   */
  sealed interface Message : Supplier<Protocol.Message> {
    /**
     * The name of this message.
     */
    val name: Name
    val documentation: Documentation?

    /**
     * The parameters of this message.
     */
    val request: AvroSchema

    val properties: ObjectProperties


    /**
     * Errors that might be thrown.
     *
     * Note: By default, avro uses a default `string` schema for the error response. This is not applicable when working
     * with jvm, so we filter for errorTypes only.
     *
     */
    val errors: AvroSchema

    fun enclosedTypes(): List<AvroSchema>
  }

  class OneWayMessage(private val message: Protocol.Message) : Message {
    override val name: Name = Name(message.name)
    override val documentation: Documentation? = Documentation.ofNullable(message.doc)

    override val request: AvroSchema = RequestType.of(message).schema
    override val properties: ObjectProperties = ObjectProperties.ofNullable(message)
    override fun get() = message

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is OneWayMessage) return false

      if (message != other.message) return false

      return true
    }

    override val errors: AvroSchema by lazy {
      // FIXME: string is a default error type in protocol, we need to filter this
      val schema = AvroSchema(message.errors)

      val errorSchemas = SchemaCatalog(schema = schema, excludeSelf = true).typesMap()
        .values.filter { it is ErrorType }.map { it.schema }

      check(errorSchemas.size < 2) { "Currently a protocol can only return 0 or 1 schemas, was: $errorSchemas." }

      if (errorSchemas.size == 1) {
        errorSchemas.single()
      } else {
        EmptyType.schema
      }
    }

    override fun hashCode(): Int = message.hashCode()

    override fun enclosedTypes(): List<AvroSchema> = listOf(request, errors)
    override fun toString() = toString("OneWayMessage") {
      add("name", name)
      addIfNotNull("documentation", documentation)
      addIfNotEmpty("properties", properties)
      add("request", request)
    }

    init {
      require(message.isOneWay) { "Message is not one-way." }
    }
  }

  class TwoWayMessage(private val message: Protocol.Message) : Message {
    override val name: Name = Name(message.name)
    override val documentation: Documentation? = Documentation.ofNullable(message.doc)
    override val request: AvroSchema = RequestType.of(message).schema
    override val properties: ObjectProperties = ObjectProperties.ofNullable(message)
    override fun get() = message

    /**
     * The returned data.
     */
    val response: MessageResponse by lazy {
      MessageResponse.of(AvroSchema(message.response))
    }

    override val errors: AvroSchema by lazy {
      // FIXME: string is a default error type in protocol, we need to filter this
      val schema = AvroSchema(message.errors)

      val errorSchemas = SchemaCatalog(schema = schema, excludeSelf = true).typesMap()
        .values.filter { it is ErrorType }.map { it.schema }

      check(errorSchemas.size < 2) { "Currently a protocol can only return 0 or 1 schemas, was: $errorSchemas." }

      if (errorSchemas.size == 1) {
        errorSchemas.single()
      } else {
        EmptyType.schema
      }
    }

    override fun enclosedTypes(): List<AvroSchema> = listOf(request, response.schema, errors)

    override fun toString(): String {
      return "TwoWayMessage(message=$message, name=$name, documentation=$documentation, request=$request, properties=$properties, response=$response, errors=$errors)"
    }

    init {
      require(!message.isOneWay) { "Message is not two-way." }
    }
  }

  /**
   * The types of this protocol.
   */
  val types: AvroTypesMap by lazy {
    AvroTypesMap(protocol = this)
  }


  val recordTypes by lazy {
    types.values.filterIsInstance<RecordType>() + types.values.filterIsInstance<ErrorType>()
  }

  /** Returns the named type.  */
  fun getType(name: Name): AvroType? = types[name]

  inline fun <reified T : AvroType> getTypeAs(name: Name) = requireNotNull(getType(name)) as T

  /**
   * The messages of this protocol.
   */
  val messages: ProtocolMessageMap<Message> by lazy {
    ProtocolMessageMap.of(protocol)
  }

  fun getMessage(name: Name): Message? = messages[name]

  /**
   * Render this as [JSON](https://json.org/).
   */
  override fun toString(): String = toString(false)
  fun toString(pretty: Boolean): String = protocol.toString(pretty)

  override fun get() = protocol

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AvroProtocol) return false

    if (protocol != other.protocol) return false

    return true
  }

  override fun hashCode(): Int = protocol.hashCode()

}
