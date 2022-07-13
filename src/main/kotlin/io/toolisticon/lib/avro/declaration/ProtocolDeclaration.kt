package io.toolisticon.lib.avro.declaration

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.ext.ProtocolExt.fqn
import io.toolisticon.lib.avro.fqn.AvroDeclarationFqn
import io.toolisticon.lib.avro.fqn.AvroDeclarationMismatchException
import io.toolisticon.lib.avro.fqn.AvroFqn
import io.toolisticon.lib.avro.fqn.ProtocolFqn
import org.apache.avro.Protocol

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
