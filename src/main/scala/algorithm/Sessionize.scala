package algorithm

import java.util.Date

import utils.TimeUtils

import scala.collection.mutable.ListBuffer

object Sessionize {
  /**
   * Transform web access data of a user.
   * Data format:
   * (client_IP:port, Iterable(client_IP:port, time, backend_IP:port))
   * input data is already sorted by time.
   *
   * @param aUser data of a user, sorted by time.
   * @return
   */
  def getSessionizedUserdata(aUser: Iterable[(String, String, String)]): String = {
    val userIdentity = aUser.head._1 // use client_IP:port here
    val accessTime = aUser.map(r => TimeUtils.parse(r._2)).iterator.toList// Time-based sessionize only need column "time"
    val sessionWindowTime = 15 * 60 * 1000 * 1000L// Session window time(in ,microsecond)
//    var sessionStrat = accessTime.head.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
    var sessionStrat = accessTime.head
    var sessionEnd = sessionStrat.plusMinutes(15)
    var sessions = List.empty[String] // Record sessionStartTime, sessionEndTime, sessionDuration(in microsecond) in (sessionStartTime@sessionEndTime@sessionDuration)
    var iterationNums = 1
    for (i <- 1 to accessTime.size - 1) {
      iterationNums+=1;
//      val timestamp = accessTime(i).atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
      val timestamp = accessTime(i)
      if (timestamp.toDate.getTime <= sessionEnd.toDate.getTime) { // Having action during sessionWindowTime
        // Extend sessionTime
        sessionEnd = timestamp.plusMinutes(15)
      } else { // Regarding as a new session.
        // Record this session time
        val aSession = sessionStrat + "@" + sessionEnd + "@" + (sessionEnd.toDate.getTime - sessionStrat.toDate.getTime)
        sessions :+= aSession
        // Find next session
        sessionStrat = timestamp
        sessionEnd = sessionStrat.plusMinutes(15)
      }
    }
    val aSession = sessionStrat + "@" + sessionEnd + "@" + (sessionEnd.toDate.getTime - sessionStrat.toDate.getTime)
    sessions :+= aSession
    // Using "#" to link each session. For example, session1StartTime@session1EndTime@session1Duration#session2StartTime@session2EndTime@session2Duration
    return sessions.mkString("#") + "$" + iterationNums + "$" + sessions.size
  }
}
