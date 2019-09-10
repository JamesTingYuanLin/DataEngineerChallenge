package utils

import java.text.{ParseException, SimpleDateFormat}
import java.util.Date

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat

object TimeUtils {
  @throws[ParseException]
  def parse(dateString: String): DateTime = {
//    val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")
//    sdf.parse(dateString)
    val dateTime = DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"))
    val utc = dateTime.withZone(DateTimeZone.UTC)
    return utc
  }
}
