package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroParser.Companion.parseSchema
import io.toolisticon.avro.kotlin.ktx.loadResource
import io.toolisticon.avro.kotlin.ktx.trailingSlash
import io.toolisticon.avro.kotlin.model.AvroSchema
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.Schema.Type
import org.apache.avro.SchemaBuilder
import java.net.URL


object TestFixtures {


  fun resourceUrl(resource: String): URL = requireNotNull(
    TestFixtures::class.java.getResource(resource.trailingSlash())
  ) { "resource not found: $resource" }


  fun loadSchemaJson(resource: String): Schema = loadResource(resource).parseSchema()

  fun loadProtocolDeclaration(resource: String): ProtocolDeclaration {
    return AvroParser().parseProtocol(loadResource(resource))
  }

  /**
   * this schema contains 5 types:
   *
   * * the root type Foo
   * * the map
   * * union of null and string
   * * null
   * * the string
   */
  val RECORD_MAP_WITH_NULLABLE_UUIDS = AvroSchema(
    schema = SchemaBuilder.record("xxx.yyy.Foo")
      .doc("This is the foo.")
      .fields()
      .name("aMap")
      .doc("this is a map with a nullable value")
      .type(
        Schema.createMap(
          Schema.createUnion(
            Schema.create(Type.NULL),
            LogicalTypes.uuid().addToSchema(Schema.create(Type.STRING))
          )
        )
      )
      .noDefault()
      .endRecord(), isRoot = true
  )

}
