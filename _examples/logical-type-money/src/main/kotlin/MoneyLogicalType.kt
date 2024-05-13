package io.toolisticon.avro.kotlin.example.money

import io.toolisticon.avro.kotlin.logical.StringLogicalType
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName

object MoneyLogicalType : StringLogicalType("money".toLogicalTypeName()) {

}
