package io.toolisticon.kotlin.avro.generator.maven

object AvroKotlinGeneratorMavenPlugin {

  const val BUILD_DIRECTORY = "\${project.build.directory}"
  const val BUILD_GENERATED_SOURCES = "$BUILD_DIRECTORY/generated-sources"
  const val BUILD_GENERATED_TEST_SOURCES = "$BUILD_DIRECTORY/generated-test-sources"


  const val DEFAULT_SOURCE_DIRECTORY = "\${project.basedir}/src/main/avro"
  const val DEFAULT_TEST_DIRECTORY = "\${project.basedir}/src/test/avro"
  const val DEFAULT_GENERATED_SOURCES = "$BUILD_GENERATED_SOURCES/avro-kotlin"
  const val DEFAULT_GENERATED_TEST_SOURCES = "$BUILD_GENERATED_TEST_SOURCES/avro-kotlin"


}
