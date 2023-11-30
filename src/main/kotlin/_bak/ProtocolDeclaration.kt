package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin._bak.ProtocolExt.fqn
import org.apache.avro.Protocol

@Deprecated("remove")
data class ProtocolDeclaration(
  override val location: AvroDeclarationFqn,
  override val content: Protocol
) : AvroDeclaration<Protocol>, AvroFqn by location {

  override val contentFqn: ProtocolFqn by lazy {
    content.fqn()
  }

  override fun verifyPackageConvention(verifyPackageConvention: Boolean): Boolean {
    if (contentFqn != location) {
      throw AvroDeclarationMismatchException(contentFqn, location)
    }
    return true
  }
}
