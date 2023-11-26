package io.toolisticon.avro.kotlin.ktx

import io.toolisticon.avro.kotlin.AvroBuilder
import org.apache.avro.Schema.*
import org.apache.avro.SchemaBuilder
import org.apache.avro.SchemaBuilder.record
import org.junit.jupiter.api.Test

internal class SchemaKtxTest {

  @Test
  fun `a reused definition can be addresses by its key`() {
    val type = record("Sub")
      .fields()
      .requiredBoolean("x")
      .requiredString("y")
      .endRecord()

    val type2 = record("Value")
      .doc("A map of Subs.")
      .fields()
      .requiredBoolean("z")
      .name("t")
      .type(createMap(type))
      .noDefault()
      .endRecord()

    val schema = SchemaBuilder.builder("foo")
      .record("Bar")
      .doc("This is the root Bar type.")
      .fields()
      .name("first").type(type)
      .noDefault()
      .name("second")
      .doc("The second field")
      .type(
        createUnion(
          AvroBuilder.primitiveSchema(Type.NULL).schema, type
        )
      )
      .noDefault()
      .name("listOfMaps")
      .type(createArray(type2))
      .noDefault()
      .endRecord()

//FIXME
//    val map = AvroTypesMap(schema)
//
//    map.entries.forEach { (k, v) ->
//      println(
//        """
//        $k:
//             $v
//
//      """.trimIndent()
//      )
  }

}
