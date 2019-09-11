package algorithm

import java.util.Date

import utils.TimeUtils

import scala.collection.mutable.ListBuffer

object Sessionize {
  /**
   * Transform web access data of a user.
   * Data format:
   * (client_IP, Iterable(client_IP:port, time, request))
   * input data is already sorted by time.
   *
   * @param aUser data of a user, sorted by time.
   * @return (IP,session1StartTime@session1EndTime@session1Duration?uniqueUrlCount#session2StartTime@session2EndTime@session2Duration??uniqueUrlCount#...$sessionCount)
   */
  def getSessionizedUserdata(aUser: Iterable[(String, String, String)]): String = {
    val userIdentity = aUser.head._1 // use client_IP:port here
    // (time, request)
    val accessTime = aUser.map(r => (TimeUtils.parse(r._2), r._3)).iterator.toList// Time-based sessionize only need column "time"
    val sessionWindowTime = 15 * 60 * 1000 * 1000L// Session window time(in ,microsecond)
//    var sessionStrat = accessTime.head.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
    var sessionStrat = accessTime.head._1
    var sessionEnd = sessionStrat.plusMinutes(15)
    var sessions = List.empty[String] // Record sessionStartTime, sessionEndTime, sessionDuration(in microsecond) in (sessionStartTime@sessionEndTime@sessionDuration)
    var urlDuringSession = Set.empty[String] + accessTime.head._2
    for (i <- 1 to accessTime.size - 1) {
//      val timestamp = accessTime(i).atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
      val timestamp = accessTime(i)._1
      val url = accessTime(i)._2
      if (timestamp.toDate.getTime <= sessionEnd.toDate.getTime) { // Having action during sessionWindowTime
        // Extend sessionTime
        sessionEnd = timestamp.plusMinutes(15)
      } else { // Regarding as a new session.
        // Record this session time
        var aSession = sessionStrat + "@" + sessionEnd + "@" + (sessionEnd.toDate.getTime - sessionStrat.toDate.getTime)
        val uniqueURLDuringSession = urlDuringSession.size
        aSession = aSession + "?" + uniqueURLDuringSession
        urlDuringSession = Set.empty[String]
        sessions :+= aSession
        // Find next session
        sessionStrat = timestamp
        sessionEnd = sessionStrat.plusMinutes(15)
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

//  def getUrlFromRequest(request: String): String = {
//    request.split(" ")(1)
//  }
}
