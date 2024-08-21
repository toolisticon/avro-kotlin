package io.toolisticon.kotlin.avro.maven

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_GENERATED_TEST_SOURCES
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_SOURCE_DIRECTORY
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_TEST_DIRECTORY
import io.toolisticon.maven.fn.FileExt.createIfNotExists
import io.toolisticon.maven.mojo.AbstractContextAwareMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.shared.model.fileset.FileSet
import org.apache.maven.shared.model.fileset.util.FileSetManager
import java.io.File

@Mojo(
  name = GenerateAvroKotlinFromSchemaMojo.GOAL,
  defaultPhase = LifecyclePhase.GENERATE_SOURCES,
  requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
  requiresProject = true
)
class GenerateAvroKotlinFromSchemaMojo : AbstractContextAwareMojo() {

  companion object {
    const val GOAL = "generate-avro-kotlin-from-schema"
  }

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
  private lateinit var sourceDirectory: File


  /**
   * The output directory will contain the final generated sources.
   */
  @Parameter(
    property = "outputDirectory",
    required = true,
    defaultValue = AvroKotlinMavenPlugin.DEFAULT_GENERATED_SOURCES
  )
  private lateinit var outputDirectory: File

  @Parameter(
    property = "testSourceDirectory",
    required = true,
    defaultValue = DEFAULT_TEST_DIRECTORY
  )
  private lateinit var testSourceDirectory: File

  /**
   * The output directory will contain the final generated sources.
   */
  @Parameter(
    property = "testOutputDirectory",
    required = true,
    defaultValue = DEFAULT_GENERATED_TEST_SOURCES
  )
  private lateinit var testOutputDirectory: File

  @Parameter(
    property = "rootFileSuffix",
    required = false,
    defaultValue = ""
  )
  private lateinit var rootFileSuffix: String

  override fun execute() {

    sanitizeParameters()

    outputDirectory.createIfNotExists()
    mojoContext.mavenProject?.addCompileSourceRoot(outputDirectory.absolutePath)

    val includes = arrayOf("**/*.avsc")

    if (!sourceDirectory.exists()) {
      log.warn("Skip non existing AVRO Schema source directory $sourceDirectory.")
      return
    }

    val includedSchemaFiles = findIncludedFiles(
      absPath = sourceDirectory.absolutePath,
      includes = includes
    )
    log.info("Found AVRO Schema ${includedSchemaFiles.size} file(s) in ${sourceDirectory}.")
    log.debug("Found AVRO Schema files: ${includedSchemaFiles.joinToString(", ")}.")

    val generatorProperties = AvroKotlinGeneratorProperties(
      schemaTypeSuffix = rootFileSuffix,
      //additionalTopLevelAnnotations = listOf() // TODO -> read this from the props.
    )

    // create file specs for each schema file.
    val generator = AvroKotlinGenerator(generatorProperties)
    val parser = AvroParser()

    val fileSpecs = includedSchemaFiles.map { File(sourceDirectory, it) }
      .map { parser.parseSchema(it) }
      .map { generator.generate(it) }


    fileSpecs.forEach {
      log.info("Generating ${outputDirectory}/${it.get().name}.kt")
      it.get().writeTo(outputDirectory)
    }
  }

  private fun findIncludedFiles(absPath: String, excludes: Array<String> = emptyArray(), includes: Array<String>): List<String> {
    val fileSetManager = FileSetManager()
    val fs = FileSet().apply {
      directory = absPath
      isFollowSymlinks = false
    }

    for (include in includes) {
      fs.addInclude(include)
    }
    for (exclude in excludes) {
      fs.addExclude(exclude)
    }
    return fileSetManager.getIncludedFiles(fs)
      .filterNotNull()
  }

  private fun sanitizeParameters() {
    // FIXME: late init seem to be not working with an empty default -> rootFileSuffix remains uninitialized
    if (!this::rootFileSuffix.isInitialized) {
      this.rootFileSuffix = ""
    }
  }
}
