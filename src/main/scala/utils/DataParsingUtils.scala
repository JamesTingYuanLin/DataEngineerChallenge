package utils

/**
 * record format:
 * (IP,session1StartTime@session1EndTime@session1Duration?uniqueUrlCount#session2StartTime@session2EndTime@session2Duration??uniqueUrlCount#...$sessionCount)
 */
object DataParsingUtils {
  val sessionDelimiter = "#"
  val sessionCountDelimiter = "$"
  val sessionStartEndDelimiter = "@"
  val uniqueUrlCountDelimiter = "?"
  val sessionTimeInfoIndex = 2

  def getTotalSessionTime(s: String): Int = {
    val withoutSessionCount = s.replace(sessionCountDelimiter + getTotalSessionCount(s), "")
    val sessionTimeInfo = withoutSessionCount.split(sessionDelimiter).map(aSession => aSession.replace(uniqueUrlCountDelimiter + getUniqueUrlCountInSession(aSession), ""))
    sessionTimeInfo.map(timeInfo => getSessionTime(timeInfo)).reduce((t1, t2) => t1 + t2)
  }

  def getTotalSessionCount(s: String): Int = {
    s.substring(s.indexOf(sessionCountDelimiter) + 1).toInt
  }

  def getSessionTime(s: String): Int = {
    s.split(sessionStartEndDelimiter)(sessionTimeInfoIndex).toInt
  }

  def getUniqueUrlCountInSession(s: String): Int = {
    s.substring(s.indexOf(uniqueUrlCountDelimiter) + 1).toInt
  }
}
