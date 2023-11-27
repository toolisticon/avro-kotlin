package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.AvroParser
import org.apache.avro.JsonProperties

/**
 * Represents a json avro declaration file or resource.
 */
@Deprecated("remove")
sealed interface AvroDeclaration<T : JsonProperties> {

  /**
   * The location derived from namespace and name.
   */
  val location: AvroDeclarationFqn

  /**
   * The parsed content.
   */
  val content: T

  /**
   * Checks if the location matches the derived content qfn.
   */
  @Throws(AvroDeclarationMismatchException::class)
  fun verifyPackageConvention(verifyPackageConvention: Boolean = AvroParser().verifyPackageConvention): Boolean
}
