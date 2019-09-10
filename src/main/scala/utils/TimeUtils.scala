package utils

import java.text.ParseException

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}

object TimeUtils {
  @throws[ParseException]
  def parse(dateString: String): DateTime = {
    val dateTime = DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"))
    val utc = dateTime.withZone(DateTimeZone.UTC)
    return utc
  }
}
