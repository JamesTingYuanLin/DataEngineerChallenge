package algorithm

import utils.TimeUtils

object Sessionize {
  /**
   * Transform web access data of a user.
   *
   * This method will find
   *   1. Each session - including startTime and endTime, sessionDuration (in millisecond)
   *   2. Unique url visited in each session
   *   3. Total session count for this user
   *
   * Here, it uses Time-oriented approaches but can only estimate roughly.
   * Assume that server will close session if the client have no more action after the last action.
   *
   * The session window time is adjustable.
   *
   * input data format:
   * (client_IP, Iterable(client_IP, time, url))
   * Note. The input data should be already sorted by time.
   *
   * @param aUser data of a user, already sorted by time.
   * @return (IP,session1StartTime@session1EndTime@session1Duration?uniqueUrlCount#session2StartTime@session2EndTime@session2Duration??uniqueUrlCount#...$sessionCount)
   */
  def getSessionizedUserdata(aUser: Iterable[(String, String, String)]): String = {
    val sessionWindowTime = 15 // in minute
    // (time, url)
    val accessTime = aUser.map(r => (TimeUtils.parse(r._2), r._3)).iterator.toList// Time-based sessionize only need column "time"
    var sessionStrat = accessTime.head._1
    var sessionEnd = sessionStrat.plusMinutes(sessionWindowTime)
    var sessions = List.empty[String] // Record sessionStartTime, sessionEndTime, sessionDuration(in microsecond) in (sessionStartTime@sessionEndTime@sessionDuration)
    var urlDuringSession = Set.empty[String] + accessTime.head._2
    for (i <- 1 to accessTime.size - 1) {
      val timestamp = accessTime(i)._1
      val url = accessTime(i)._2
      if (timestamp.toDate.getTime <= sessionEnd.toDate.getTime) { // Having action during sessionWindowTime
        // Extend sessionTime
        sessionEnd = timestamp.plusMinutes(sessionWindowTime)
      } else { // Regarding as a new session.
        // Record this session time (this session is ended)
        var aSession = sessionStrat + "@" + sessionEnd + "@" + (sessionEnd.toDate.getTime - sessionStrat.toDate.getTime)
        val uniqueURLDuringSession = urlDuringSession.size
        aSession = aSession + "?" + uniqueURLDuringSession
        urlDuringSession = Set.empty[String]
        sessions :+= aSession
        // Find next session (start a new session)
        sessionStrat = timestamp
        sessionEnd = sessionStrat.plusMinutes(sessionWindowTime)
      }
      urlDuringSession += url
    }
    var aSession = sessionStrat + "@" + sessionEnd + "@" + (sessionEnd.toDate.getTime - sessionStrat.toDate.getTime)
    val uniqueURLDuringSession = urlDuringSession.size
    aSession = aSession + "?" + uniqueURLDuringSession
    sessions :+= aSession
    // Using "#" to link each session. For example, session1StartTime@session1EndTime@session1Duration#session2StartTime@session2EndTime@session2Duration$sessionCount
    return sessions.mkString("#") + "$" + sessions.size
  }
}
