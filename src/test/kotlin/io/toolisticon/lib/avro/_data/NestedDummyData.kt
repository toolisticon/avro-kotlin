package io.toolisticon.lib.avro._data

import com.github.avrokotlin.avro4k.AvroName
import com.github.avrokotlin.avro4k.AvroNamespace
import com.github.avrokotlin.avro4k.ScalePrecision
import com.github.avrokotlin.avro4k.serializer.BigDecimalSerializer
import com.github.avrokotlin.avro4k.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class NestedDummyData(
  @Serializable(with = UUIDSerializer::class) val id: UUID,
  val type: List<DummyTypeData>,
  val message: String? = null,
  val money: NestedMoneyData
)

@AvroName("DummyType")
@Serializable
enum class DummyTypeData {
  FOO, BAR
}


@Serializable
data class NestedMoneyData(
  @ScalePrecision(scale = 2, precision = 10)
  @Serializable(with = BigDecimalSerializer::class)
  val amount: BigDecimal,
  val currencyCode: String
)
