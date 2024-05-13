package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.Protocol
import java.util.function.Supplier

/**
 * Provides access to a wrapped [Protocol].
 */
interface ProtocolSupplier : Supplier<Protocol> {

  /**
   * The hashCode identifies a [Protocol].
   */
  val hashCode: AvroHashCode

  /**
   * The JSON representation of this avro declaration.
   */
  val json: JsonString

  /**
   * The name of the protocol.
   */
  val name: Name

  /**
   * Namespace of [Protocol]. In the official specification this is optional but for practical reasons,
   * it should be set, so it is required here.
   */
  val namespace: Namespace

  val canonicalName: CanonicalName get() = namespace + name
}
