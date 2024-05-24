package _ktx

import _ktx.StringKtx.removeSeparatorPrefix
import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.JsonString
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.Path

object ResourceKtx {

  fun loadJsonString(resource: String, path: String? = null, classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER): JsonString =
    JsonString.of(
      json = resourceUrl(resource, path, classLoader).readText(AvroKotlin.UTF_8).trim()
    )

  /**
   * Gets a resource URL from static `resources` folder.
   *
   * @param resource the resource to get
   * @param classLoader optional classloader, defaults to DEFAULT_CLASSLOADER
   * @return url of given resource
   * @throws IllegalArgumentException when resource does not exist
   */
  @Throws(IllegalArgumentException::class)
  fun resourceUrl(
    resource: String,
    path: String? = null,
    classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER
  ): URL = resourceUrl(resourcePath = Path(path ?: "").resolve(resource), classLoader = classLoader)

  fun resourceUrl(resourcePath: Path, classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER): URL =
    with(resourcePath.toString().removeSeparatorPrefix()) {
      requireNotNull(classLoader.getResource(this)) { "Resource not found: $this" }
    }

  @Throws(IllegalStateException::class)
  fun rootResource(classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER): URL = checkNotNull(
    classLoader.getResource("")
  ) { "no root resource found for classLoader:$classLoader" }

  fun findAvroResources(prefix: String? = null, classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER): Map<AvroSpecification, List<File>> {
    val rootPath = Path(rootResource(classLoader).path).resolve(prefix ?: "")

    return rootPath.toFile().walk()
      .filter { it.isFile }
      .filter { AvroSpecification.EXTENSIONS.contains(it.extension) }
      .groupBy(keySelector = { AvroSpecification.valueOfExtension(it.extension) })
  }
}
