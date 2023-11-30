package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin._bak.SchemaExt.fqn
import org.apache.avro.Schema

@Deprecated("remove")
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
