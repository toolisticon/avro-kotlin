package io.toolisticon.lib.avro.declaration

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.ext.SchemaExt.fqn
import io.toolisticon.lib.avro.fqn.AvroDeclarationFqn
import io.toolisticon.lib.avro.fqn.AvroDeclarationMismatchException
import io.toolisticon.lib.avro.fqn.AvroFqn
import io.toolisticon.lib.avro.fqn.SchemaFqn
import org.apache.avro.Schema

data class SchemaDeclaration(
  override val location: AvroDeclarationFqn,
  override val content: Schema
) : AvroDeclaration<Schema>, AvroFqn by location {

  constructor(schema: Schema) : this(location = schema.fqn(), content = schema)

  override val contentFqn: SchemaFqn by lazy {
    content.fqn()
  }

  override fun verifyPackageConvention(verifyPackageConvention: Boolean): Boolean {
    if (contentFqn != location) {
      throw AvroDeclarationMismatchException(contentFqn, location)
    }
    return true
  }
}
