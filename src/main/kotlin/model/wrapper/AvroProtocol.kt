package io.toolisticon.avro.kotlin.model.wrapper

import _ktx.StringKtx.firstUppercase
import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.*
import io.toolisticon.avro.kotlin.value.*
import io.toolisticon.avro.kotlin.value.AvroFingerprint.Companion.sum
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

    fun requestName(message: Protocol.Message): Name = Name("${message.name.firstUppercase()}Request")

    fun schemaForMessageRequest(message: Protocol.Message) = if (message.request.fields.isEmpty()) {
      EmptyType.schema
    } else {
      AvroSchema(schema = message.request, name = requestName(message))
    }
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
  override val documentation = AvroKotlin.documentation(protocol.doc)
  override val properties = ObjectProperties(protocol)

  val md5: ByteArray by lazy { protocol.mD5 }

  /**
   * [Protocol] does not have a [AvroFingerprint]. To identify an identical
   * protocol, we can calculate the fingerprint using all types and messages.
   */
  val fingerprint: AvroFingerprint by lazy {
    buildList {
      add(AvroFingerprint(protocol.namespace))
      add(AvroFingerprint(protocol.name))
      addAll(protocol.types.map { AvroFingerprint(it) })
      addAll(protocol.messages.values.map { AvroFingerprint(it) })
    }.sum()
  }

  override val json: JsonString by lazy { JsonString(protocol) }

  override val hashCode = AvroHashCode(protocol)

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

    fun enclosedTypes(): List<AvroSchema>
  }

  class OneWayMessage(private val message: Protocol.Message) : Message {
    override val name: Name = Name(message.name)
    override val documentation: Documentation? = AvroKotlin.documentation(message.doc)

    override val request: AvroSchema = schemaForMessageRequest(message)

    override val properties: ObjectProperties = ObjectProperties(message)
    override fun get() = message
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is OneWayMessage) return false

      if (message != other.message) return false

      return true
    }

    override fun hashCode(): Int = message.hashCode()

    override fun enclosedTypes(): List<AvroSchema> = listOf(request)
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
    override val documentation: Documentation? = AvroKotlin.documentation(message.doc)
    override val request: AvroSchema = schemaForMessageRequest(message)
    override val properties: ObjectProperties = ObjectProperties(message)
    override fun get() = message

    /**
     * The returned data.
     */
    val response: AvroSchema = AvroSchema(message.response)

    /**
     * Errors that might be thrown.
     */
    val errors: AvroSchema by lazy {
      // FIXME: string is a default error type in protocol, we need to filter this
      val schema = AvroSchema(message.errors)
      val errorTypes = schema.enclosedTypes

      if (errorTypes.size > 1) {
        schema.enclosedTypes.filterNot { it.isPrimitive }.single()
      } else {
        schema
      }
    }

    override fun enclosedTypes(): List<AvroSchema> = listOf(request, response, errors)

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
  val messages: Map<Name, Message> by lazy {
    protocol.messages.values.map {
      if (it.isOneWay) {
        OneWayMessage(it)
      } else {
        TwoWayMessage(it)
      }
    }.associateBy { it.name }
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
