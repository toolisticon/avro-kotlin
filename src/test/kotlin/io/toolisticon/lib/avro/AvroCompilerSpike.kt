package io.toolisticon.lib.avro

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.apache.avro.Schema
import org.apache.avro.compiler.specific.SpecificCompiler
import java.io.File
import java.nio.file.Files
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class AvroCompilerSpike {


}

fun main() {

  val basePath = "/Users/jangalinski/IdeaProjects/toolisticon/avro-kotlin"


  val schema = Schema.Parser().parse("""
    {
      "namespace": "lib.test.event",
      "name": "BankAccountCreated",
      "type": "record",
      "fields": [
        {
          "name": "bankAccountId",
          "type": {
            "type": "string"
          }
        },
        {
          "name": "initialBalance",
          "type": {
            "type": "int"
          }
        }
      ]
    }
  """.trimIndent())



  println(schema)

  val compiler = SpecificCompiler(schema).apply {
    setTemplateDir("$basePath/src/test/resources/templates/")
    setSuffix(".kt")
  }.compileToDestination(null, File("$basePath/target/GEN"))

}
