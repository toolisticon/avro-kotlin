package io.toolisticon.lib.avro.declaration

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.fqn.AvroDeclarationFqn
import io.toolisticon.lib.avro.fqn.AvroDeclarationMismatchException
import io.toolisticon.lib.avro.fqn.AvroFqn
import org.apache.avro.JsonProperties
import kotlin.jvm.Throws

/**
 * Represents a json avro declaration file or resource.
 */
sealed interface AvroDeclaration<T: JsonProperties> : AvroFqn {

  /**
   * The location derived from namespace and name.
   */
  val location: AvroDeclarationFqn

  /**
   * The parsed content.
   */
  val content: T

  /**
   * The fqn derived from the parsed [content].
   */
  val contentFqn: AvroFqn

  /**
   * Checks if the location matches the derived content qfn.
   */
  @Throws(AvroDeclarationMismatchException::class)
  fun verifyPackageConvention(verifyPackageConvention: Boolean = AvroKotlinLib.verifyPackageConvention) : Boolean
}
