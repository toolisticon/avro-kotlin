package io.toolisticon.kotlin.avro.maven

import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.CodeFormatter
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_GENERATED_TEST_SOURCES
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_SOURCE_DIRECTORY
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_TEST_DIRECTORY
import io.toolisticon.maven.mojo.AbstractContextAwareMojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File

sealed class AbstractGenerateAvroKotlinMojo : AbstractContextAwareMojo() {

  /**
   * The source directory of avro files. This directory is added to the classpath
   * at schema compiling time. All files can therefore be referenced as classpath
   * resources following the directory structure under the source directory.
   */
  @Parameter(
    property = "sourceDirectory",
    defaultValue = DEFAULT_SOURCE_DIRECTORY,
    required = false,
    readonly = true
  )
  protected lateinit var sourceDirectory: File

  /**
   * The output directory will contain the final generated sources.
   */
  @Parameter(
    property = "outputDirectory",
    required = true,
    defaultValue = AvroKotlinMavenPlugin.DEFAULT_GENERATED_SOURCES
  )
  protected lateinit var outputDirectory: File

  @Parameter(
    property = "testSourceDirectory",
    required = true,
    defaultValue = DEFAULT_TEST_DIRECTORY
  )
  protected lateinit var testSourceDirectory: File

  /**
   * The output directory will contain the final generated sources.
   */
  @Parameter(
    property = "testOutputDirectory",
    required = true,
    defaultValue = DEFAULT_GENERATED_TEST_SOURCES
  )
  protected lateinit var testOutputDirectory: File

  @Parameter(
    property = "formatter",
    required = false,
    defaultValue = "NONE"
  )
  protected lateinit var formatter : CodeFormatter

  @Parameter(
    property = "rootFileSuffix",
    required = false,
    defaultValue = ""
  )
  protected lateinit var rootFileSuffix: String

  protected fun sanitizeParameters() {
    // FIXME: late init seem to be not working with an empty default -> rootFileSuffix remains uninitialized
    if (!this::rootFileSuffix.isInitialized) {
      this.rootFileSuffix = ""
    }
  }
}
