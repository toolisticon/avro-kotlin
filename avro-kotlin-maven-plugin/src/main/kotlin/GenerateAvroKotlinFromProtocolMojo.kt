package io.toolisticon.kotlin.avro.maven

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.DefaultAvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_GENERATED_TEST_SOURCES
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_SOURCE_DIRECTORY
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.DEFAULT_TEST_DIRECTORY
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.findIncludedFiles
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.writeToFormatted
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
  name = GenerateAvroKotlinFromProtocolMojo.GOAL,
  defaultPhase = LifecyclePhase.GENERATE_SOURCES,
  requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
  requiresProject = true
)
class GenerateAvroKotlinFromProtocolMojo : AbstractGenerateAvroKotlinMojo() {

  companion object {
    const val GOAL = "generate-avro-kotlin-from-protocol"
  }

  override fun execute() {
    sanitizeParameters()

    outputDirectory.createIfNotExists()
    mojoContext.mavenProject?.addCompileSourceRoot(outputDirectory.absolutePath)

    val includes = arrayOf("**/*.avpr")

    if (!sourceDirectory.exists()) {
      log.warn("Skip non existing AVRO source directory $sourceDirectory.")
      return
    }

    val includedSchemaFiles = findIncludedFiles(
      absPath = sourceDirectory.absolutePath,
      includes = includes
    )
    log.info("Found AVRO Protocol ${includedSchemaFiles.size} file(s) in ${sourceDirectory}.")
    log.debug("Found AVRO Protocol files: ${includedSchemaFiles.joinToString(", ")}.")

    val generatorProperties = DefaultAvroKotlinGeneratorProperties(
      schemaTypeSuffix = rootFileSuffix,
      //additionalTopLevelAnnotations = listOf() // TODO -> read this from the props.
    )

    // create file specs for each schema file.
    val generator = AvroKotlinGenerator(generatorProperties)
    val parser = AvroParser()

    val fileSpecs = includedSchemaFiles.map { File(sourceDirectory, it) }
      .map { parser.parseProtocol(it) }
      .flatMap { generator.generate(it) }

    fileSpecs.forEach {
      val file = formatter(outputDirectory, it)
      log.info("Generating: $file")
    }
  }
}
