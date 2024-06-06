package io.toolisticon.kotlin.avro.generator.maven

import com.soebes.itf.extension.assertj.MavenITAssertions.assertThat
import com.soebes.itf.jupiter.extension.*
import com.soebes.itf.jupiter.maven.MavenExecutionResult
import org.junit.jupiter.api.Disabled

@Disabled("FIXME: does not work")
@MavenJupiterExtension
@MavenRepository
@MavenOptions(
  MavenOption("--no-transfer-progress")
)
class GenerateAvroKotlinFromSchemaMojoIT {

  @MavenTest
  fun warn_on_missing_source_directory(result: MavenExecutionResult) {
    assertThat(result).isSuccessful()
    assertThat(result)
      .out()
      .warn()
      .isNotEmpty
      .anySatisfy { line ->
        assertThat(line).isEqualTo("Skip non existing AVRO Schema source directory ${result.mavenProjectResult.targetProjectDirectory}/src/main/avro.")
      }
  }

  @MavenTest
  fun generates_data_classes(result: MavenExecutionResult) {
    val targetDir = "generated-sources/avro-kotlin"
    val targetBasePackage = "io.toolisticon.kotlin.avro.generator.schema".replace(".", "/")
    assertThat(result).isSuccessful()
    assertThat(result).out()
      .info()
      .isNotEmpty
      .anySatisfy { line ->
        assertThat(line).isEqualTo("Found AVRO Schema 2 file(s) in ${result.mavenProjectResult.targetProjectDirectory}/src/main/avro.")
      }
    assertThat(result).project().hasTarget().withFile("$targetDir/$targetBasePackage/SimpleTypesRecordData.kt").exists()
    //.hasSameTextualContentAs(
    //  File(result.mavenProjectResult.targetProjectDirectory, "target/SimpleTypesRecordData.kt")
    //)
    assertThat(result).project().hasTarget().withFile("$targetDir/$targetBasePackage/enumeration/SchemaContainingEnumData.kt").exists()
      //.hasSameTextualContentAs(
      //  File(result.mavenProjectResult.targetProjectDirectory, "target/SchemaContainingEnumData.kt")
      //)
  }
}

