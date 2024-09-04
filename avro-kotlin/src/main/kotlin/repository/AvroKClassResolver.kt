package io.toolisticon.kotlin.avro.repository

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import org.apache.avro.AvroRuntimeException
import org.apache.avro.specific.SpecificData
import kotlin.reflect.KClass


interface AvroKClassByFingerprintResolver {

  /**
   * Find a `KClass<T>` for a fingerprint.
   *
   * @see get(AvroSchema) in case this function uses [AvroSchemaResolver] and schema data for lookup.
   * @param fingerprint - the schema fingerprint
   * @return found `KClass<T>`
   * @throws AvroRuntimeException when find fails
   */
  @Throws(AvroRuntimeException::class)
  operator fun <T : Any> get(fingerprint: AvroFingerprint): KClass<T>
}

interface AvroKClassBySchemaResolver {

  /**
   * Find a `KClass<T>` for a schema.
   *
   * @param schema - the schema (holding fqn and fingerprint)
   * @return found `KClass<T>`
   * @throws AvroRuntimeException when find fails
   */
  @Throws(AvroRuntimeException::class)
  operator fun <T : Any> get(schema: AvroSchema): KClass<T>

}

/**
 * AvroKClassResolver is a strategic interface to get the KClass reference for a given schema (or fingerprint).
 *
 * When deserializing bytes (single object, json, ...), we not only need the writer schema (which we can get using the fingerprint encoded in the single
 * object encoded bytes for example), we also need the type (KClass) of the target we deserialize to, as we are going to create an instance of that type and
 * return it as generic type variable `<T>`.
 *
 * In case of a "flat class file" this is an easy task, we just derive the schema for the fingerprint and use `Class.forName` with the canonical name of
 * the schema. But when we use code generation to generated nested types (for grouping, common sealed interfaces, ...) the canonical name does not
 * necessarily match the schema name anymore. In some cases using the `SerialName` annotation on a generated class could help here, in other cases this
 * might require a different lookup strategy (dictionary, reflection, ...).
 */
interface AvroKClassResolver : AvroKClassByFingerprintResolver, AvroKClassBySchemaResolver

/**
 * A [AvroKClassResolver] that takes a list of resolvers and invokes them one by one until a `KCLass` is found.
 * If all resolvers are failing, the resulting [ClassNotFoundException] contains all failed messages.
 */
class CompositeAvroKClassResolver(private val resolvers: List<AvroKClassResolver>) : AvroKClassResolver {
  private fun <T : Any> resolveTilFound(fns: List<() -> KClass<T>>): KClass<T> {
    val caughtMessages = mutableListOf<String>()

    for (fn in fns) {
      try {
        return fn.invoke()
      } catch (e: AvroRuntimeException) {
        e.message?.let { caughtMessages.add(it) }
      }
    }
    throw AvroRuntimeException(caughtMessages.joinToString())
  }

  override fun <T : Any> get(fingerprint: AvroFingerprint): KClass<T> = resolveTilFound(resolvers.map { { it.get<T>(fingerprint) } })

  override fun <T : Any> get(schema: AvroSchema): KClass<T> = resolveTilFound(resolvers.map { { it.get<T>(schema) } })

}

object DefaultAvroKClassResolvers {

  fun specificDataKClassResolver(specificData: SpecificData = AvroKotlin.specificData) = object : AvroKClassBySchemaResolver {

    override fun <T : Any> get(schema: AvroSchema): KClass<T> {
      val nullableJavaClassClass: Class<*>? = specificData.getClass(schema.get())

      @Suppress("UNCHECKED_CAST")
      return if (nullableJavaClassClass != null)
        nullableJavaClassClass.kotlin as KClass<T>
      else throw AvroRuntimeException("KClass could not be found for fqn=`${schema.canonicalName.fqn}`.")
    }
  }

  /**
   * Uses [specificDataKClassResolver] but can also resolve a schema by fingerprint first.
   */
  fun resolvingSpecificDataKClassResolver(
    schemaResolver: AvroSchemaResolver,
    specificData: SpecificData = AvroKotlin.specificData
  ): AvroKClassResolver = object : AvroKClassResolver {
    private val specificDataKClassResolver = specificDataKClassResolver(specificData)

    override fun <T : Any> get(fingerprint: AvroFingerprint): KClass<T> = try {
      get(schemaResolver[fingerprint])
    } catch (e: AvroRuntimeException) {
      throw AvroRuntimeException("KClass could not be found for fingerprint=`${fingerprint.hex.formatted}`.")
    }

    override fun <T : Any> get(schema: AvroSchema): KClass<T> = specificDataKClassResolver[schema]
  }
}
