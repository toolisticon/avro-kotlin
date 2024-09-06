package io.toolisticon.kotlin.avro.maven

import com.facebook.ktfmt.format.Formatter
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import org.apache.maven.shared.model.fileset.FileSet
import org.apache.maven.shared.model.fileset.util.FileSetManager
import java.io.File

object AvroKotlinMavenPlugin {

  const val BUILD_DIRECTORY = "\${project.build.directory}"
  const val BUILD_GENERATED_SOURCES = "$BUILD_DIRECTORY/generated-sources"
  const val BUILD_GENERATED_TEST_SOURCES = "$BUILD_DIRECTORY/generated-test-sources"

  const val DEFAULT_SOURCE_DIRECTORY = "\${project.basedir}/src/main/avro"
  const val DEFAULT_TEST_DIRECTORY = "\${project.basedir}/src/test/avro"
  const val DEFAULT_GENERATED_SOURCES = "$BUILD_GENERATED_SOURCES/avro-kotlin"
  const val DEFAULT_GENERATED_TEST_SOURCES = "$BUILD_GENERATED_TEST_SOURCES/avro-kotlin"

  enum class CodeFormatter : (File, KotlinFileSpec) -> File {
    KTFMT {
      override fun invoke(outputDirectory: File, fileSpec: KotlinFileSpec): File = fileSpec.writeToFormatted(outputDirectory)
    },
    NONE {
      override fun invoke(outputDirectory: File, fileSpec: KotlinFileSpec): File = fileSpec.get().writeTo(outputDirectory)
    }
  }

  /**
   * Format of generated code.
   */
  val KTFMT_FORMAT = Formatter.KOTLINLANG_FORMAT.copy(
    maxWidth = 256,
    blockIndent = 2,
    continuationIndent = 2
  )

  /**
   * Uses [com.squareup.kotlinpoet.FileSpec.writeTo] but applies
   * [Formatter.format] on the created file.
   *
   * @param outputDirectory root directory to write to
   * @return the written formatted file.
   */
  fun KotlinFileSpec.writeToFormatted(outputDirectory: File): File {
    val file = this.get().writeTo(outputDirectory)

    val formattedCode = Formatter.format(KTFMT_FORMAT, file.readText())

    file.writeText(formattedCode)

    return file
  }


  fun findIncludedFiles(absPath: String, excludes: Array<String> = emptyArray(), includes: Array<String>): List<String> {
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
    return fileSetManager.getIncludedFiles(fs).filterNotNull()
  }
}
