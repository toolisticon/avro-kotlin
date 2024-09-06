package io.toolisticon.kotlin.avro.maven

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.DefaultAvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.maven.AvroKotlinMavenPlugin.findIncludedFiles
import io.toolisticon.maven.fn.FileExt.createIfNotExists
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import java.io.File

@Mojo(
  name = GenerateAvroKotlinFromSchemaMojo.GOAL,
  defaultPhase = LifecyclePhase.GENERATE_SOURCES,
  requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
  requiresProject = true
)
class GenerateAvroKotlinFromSchemaMojo : AbstractGenerateAvroKotlinMojo() {
  companion object {
    const val GOAL = "generate-avro-kotlin-from-schema"
  }

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

    val generatorProperties = DefaultAvroKotlinGeneratorProperties(
      schemaTypeSuffix = rootFileSuffix,
      //additionalTopLevelAnnotations = listOf() // TODO -> read this from the props.
    )

    // create file specs for each schema file.
    val generator = AvroKotlinGenerator(generatorProperties)
    val parser = AvroParser()

    val fileSpecs = includedSchemaFiles.map { File(sourceDirectory, it) }
      .map { parser.parseSchema(it) }
      .flatMap { generator.generate(it) }

    fileSpecs.forEach {
      val file = formatter(outputDirectory, it)
      log.info("Generating: $file")
    }
  }


}
