package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro._test.FooString
import io.toolisticon.kotlin.avro._test.FooString2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class GenericRecordConverterTest {

  @Test
  fun `convert fooString v1 to v2`() {
    val v1 = FooString(str = "value").toGenericRecord()

    val v2 = GenericRecordConverter(FooString2.SCHEMA).convert(v1)

    with(FooString2(v2)) {
      assertThat(str).isEqualTo("value")
      assertThat(uuid).isNull()
    }
  }
}
