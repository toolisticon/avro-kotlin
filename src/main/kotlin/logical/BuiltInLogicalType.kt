package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion
import org.apache.avro.Conversions
import org.apache.avro.data.TimeConversions
import java.time.Duration
import kotlin.jvm.Throws

/**
 * Represents all 10 logical types that are defined by the [official avro implementation](https://avro.apache.org/docs/1.11.1/specification/#logical-types).
 */
enum class BuiltInLogicalType(
  val logicalTypeName: LogicalTypeName,
  val conversion: Conversion<*>,

  /**
   * marks the type to be excluded, see DURATION.
   */
  private val excludeConversion: Boolean = false,
) {

  /**
   * The date logical type represents a date within the calendar, with no reference to a particular time zone or time of day.
   *
   * A date logical type annotates an Avro int, where the int stores the number of days from the unix epoch, 1 January 1970 (ISO calendar).
   *
   * Converts [java.time.LocalDate] for logicalTypeName=`date`.
   */
  DATE(
    logicalTypeName = LogicalTypeName("date"),
    conversion = TimeConversions.DateConversion()
  ),

  /**
   * The decimal logical type represents an arbitrary-precision signed decimal number of the form unscaled × 10-scale.
   *
   * Converts [java.math.BigDecimal] for logicalTypeName=`decimal`.
   */
  DECIMAL(
    logicalTypeName = LogicalTypeName("decimal"),
    conversion = Conversions.DecimalConversion()
  ),

  /**
   * The duration logical type represents an amount of time defined by a number of months, days and milliseconds.
   * This is not equivalent to a number of milliseconds, because, depending on the moment in time from which the
   * duration is measured, the number of days in the month and number of milliseconds in a day may differ.
   * Other standard periods such as years, quarters, hours and minutes can be expressed through these basic periods.
   *
   * A duration logical type annotates Avro fixed type of size 12, which stores three little-endian unsigned integers
   * that represent durations at different granularities of time. The first stores a number in months, the second stores
   * a number in days, and the third stores a number in milliseconds.
   *
   * @Deprecated("Duration is not supported by apache-avro-java.")
   */
  DURATION(
    logicalTypeName = LogicalTypeName("duration"),
    conversion = object : Conversion<Duration>() {
      override fun getConvertedType(): Class<Duration> = Duration::class.java
      override fun getLogicalTypeName(): String = "duration"
    },
    excludeConversion = true
  ),

  /**
   * The local-timestamp-micros logical type represents a timestamp in a local timezone, regardless of what specific
   * time zone is considered local, with a precision of one microsecond.
   *
   * A local-timestamp-micros logical type annotates an Avro long, where the long stores the number of microseconds,
   * from 1 January 1970 00:00:00.000000.
   *
   * Converts [java.time.LocalDateTime] for logicalTypeName=`local-timestamp-micros`.
   */
  LOCAL_TIMESTAMP_MICROS(
    logicalTypeName = LogicalTypeName("local-timestamp-micros"),
    conversion = TimeConversions.LocalTimestampMicrosConversion()
  ),

  /**
   * The local-timestamp-millis logical type represents a timestamp in a local timezone, regardless of what specific
   * time zone is considered local, with a precision of one millisecond.
   *
   * A local-timestamp-millis logical type annotates an Avro long, where the long stores the number of milliseconds,
   * from 1 January 1970 00:00:00.000.
   *
   * Converts [java.time.LocalDateTime] for logicalTypeName=`local-timestamp-millis`.
   */
  LOCAL_TIMESTAMP_MILLIS(
    logicalTypeName = LogicalTypeName("local-timestamp-millis"),
    conversion = TimeConversions.LocalTimestampMillisConversion()
  ),

  /**
   * The time-micros logical type represents a time of day, with no reference to a particular calendar,
   * time zone or date, with a precision of one microsecond.
   *
   * A time-micros logical type annotates an Avro long, where the long stores the number of microseconds after
   * midnight, 00:00:00.000000.
   *
   * Converts [java.time.LocalTime] for logicalTypeName=`time-micros`.
   */
  TIME_MICROS(
    logicalTypeName = LogicalTypeName("time-micros"),
    conversion = TimeConversions.TimeMicrosConversion()
  ),

  /**
   * The time-millis logical type represents a time of day, with no reference to a particular calendar, time zone or date,
   * with a precision of one millisecond.
   *
   * A time-millis logical type annotates an Avro int, where the int stores the number of milliseconds
   * after midnight, 00:00:00.000.
   *
   * Converts [java.time.LocalTime] for logicalTypeName=`time-millis`.
   */
  TIME_MILLIS(
    logicalTypeName = LogicalTypeName("time-millis"),
    conversion = TimeConversions.TimeMillisConversion()
  ),

  /**
   * The timestamp-micros logical type represents an instant on the global timeline, independent of a particular time
   * zone or calendar, with a precision of one microsecond. Please note that time zone information gets lost in this
   * process. Upon reading a value back, we can only reconstruct the instant, but not the original representation.
   *
   * In practice, such timestamps are typically displayed to users in their local time zones, therefore they may be
   * displayed differently depending on the execution environment.
   *
   * A timestamp-micros logical type annotates an Avro long, where the long stores the number of microseconds from the
   * unix epoch, 1 January 1970 00:00:00.000000 UTC.
   *
   * Converts [java.time.Instant] for logicalTypeName=`timestamp-micros`.
   */
  TIMESTAMP_MICROS(
    logicalTypeName = LogicalTypeName("timestamp-micros"),
    conversion = TimeConversions.TimestampMicrosConversion()
  ),

  /**
   * The timestamp-millis logical type represents an instant on the global timeline, independent of a particular
   * time zone or calendar, with a precision of one millisecond. Please note that time zone information gets
   * lost in this process. Upon reading a value back, we can only reconstruct the instant, but not the original
   * representation. In practice, such timestamps are typically displayed to users in their local time zones,
   * therefore they may be displayed differently depending on the execution environment.
   *
   * A timestamp-millis logical type annotates an Avro long, where the long stores the number of milliseconds from
   * the unix epoch, 1 January 1970 00:00:00.000 UTC.
   *
   * Converts [java.time.Instant] for logicalTypeName=`timestamp-micros`.
   */
  TIMESTAMP_MILLIS(
    logicalTypeName = LogicalTypeName("timestamp-millis"),
    conversion = TimeConversions.TimestampMillisConversion()
  ),

  /**
   * The uuid logical type represents a random generated universally unique identifier (UUID).
   *
   * An uuid logical type annotates an Avro string. The string has to conform with RFC-4122.
   *
   * Converts [java.util.UUID] for logicalTypeName=`uuid`.
   */
  UUID(
    logicalTypeName = LogicalTypeName("uuid"),
    conversion = Conversions.UUIDConversion()
  ),
  ;

  /**
   * The converted type of this logical type as defined in [conversion].
   */
  val convertedType: Class<*> by lazy {
    conversion.convertedType
  }

  companion object {
    private val BY_NAME = entries.associateBy { it.logicalTypeName }

    /**
     * Get enum value by logicalTypeName.
     *
     * @param logicalTypeName the wrapped logical type name
     * @return enum value with given logicalType.
     * @throws IllegalArgumentException if no built-in type exists
     */
    @Throws(IllegalArgumentException::class)
    fun valueOfLogicalTypeName(logicalTypeName: LogicalTypeName) = requireNotNull(
      BY_NAME[logicalTypeName]
    ) { "No built-in type found for logicalTypeName=$logicalTypeName" }

    /**
     * List of all supported [Conversion<*>]s defined by avro core [org.apache.avro.LogicalType]s.
     */
    val CONVERSIONS = entries.filterNot {
      it.excludeConversion
    }.map(BuiltInLogicalType::conversion)
      .toList()
  }
}
