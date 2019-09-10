package utils

object DataParsingUtils {
  val sessionDelimiter = "#"
  val sessionCountDelimiter = "$"
  val sessionStartEndDelimiter = "@"
  val sessionTimeInfoIndex = 2

  def getTotalSessionTime(s: String): Int = {
    val withoutSessionCount = s.replace(sessionCountDelimiter + getTotalSessionCount(s), "")
    withoutSessionCount.split(sessionDelimiter).map(timeInfo => getSessionTime(timeInfo)).reduce((t1, t2) => t1 + t2)
  }

  def getTotalSessionCount(s: String): Int = {
    s.substring(s.indexOf(sessionCountDelimiter) + 1).toInt
  }

  def getSessionTime(s: String): Int = {
    s.split(sessionStartEndDelimiter)(sessionTimeInfoIndex).toInt
  }
}
