package app

import algorithm.Sessionize
import etl.schema.csvColumns.cleandata.{SessionizedData, TotalSessionTimeAndCountPerUser}
import etl.schema.csvColumns.rawdata.AWSElasticLoadBalancerLog
import org.apache.spark.rdd.{PairRDDFunctions, RDD}
import org.apache.spark.{SparkConf, SparkContext}
import utils.{DataParsingUtils, StringUtils}

import scala.reflect.ClassTag

object Test {
  def main(args: Array[String]): Unit = {
    /*-------- IO Path & file name --------*/
    val inputFilePath = args(0)
    val outputFilePath = args(1)
    val sortedByIpAndTimeFileName = "sortByIpAndTime"
    val sessionizedDataFileName = "sessionizedData"
    val avgSessionTimeFileName = "avgSessionTime"
    val totalSessionTimeAndCountPerUserFileName = "totalSessionTimeAndCountPerUser"
    /*-------- IO Path & file name --------*/

    val conf = new SparkConf()
    val sc = new SparkContext()

    val data = sc.textFile(inputFilePath)

    /*
     * group by user identity. Use Ip address as id. (remove port)
     * (client_IP, time, url)
     */
    val filterColumns = data.map(line => {
      ((StringUtils.removePort(line.split(" ")(AWSElasticLoadBalancerLog.clientIpWithPort.index)),
        line.split(" ")(AWSElasticLoadBalancerLog.time.index)),
        line.split(" ")(AWSElasticLoadBalancerLog.url.index))
    })

    // Sortby both Ip and and time
    val sortByUserAndTime = filterColumns.sortBy(_._1)
    sortByUserAndTime.saveAsTextFile(outputFilePath + sortedByIpAndTimeFileName)

    /*
     * Plaint data grouped by user
     * (client_IP, Iterable(client_IP:port, time, url))
     */
    val groupByUser = sortByUserAndTime.map(r => (r._1._1, r._1._2, r._2)).groupBy(r => r._1)

    /*
     * Sessionized data (by each user, calculate startTime, endTime, session duration, uniqueUrl, session count)
     * Sessionized data format: (IP,session1StartTime@session1EndTime@session1Duration?uniqueUrlCount#session2StartTime@session2EndTime@session2Duration??uniqueUrlCount#...$sessionCount)
     * (IP, sessionizedData)
     */
    val userSessions = groupByUser.map(aUser => {
      aUser._1 + "," + Sessionize.getSessionizedUserdata(aUser._2)
    })
    userSessions.repartition(1).saveAsTextFile(outputFilePath + sessionizedDataFileName) // output the sessionized data

    /*
     * Find average session time
     */
    val totalSessiontime = sc.accumulator(0)
    val totalSessionCount = sc.accumulator(0)
    val sessionTimes = userSessions.map(aUser => DataParsingUtils.getTotalSessionTime(aUser.split(",")(SessionizedData.sessionizedData.index)))
    val sessionCounts = userSessions.map(aUser => DataParsingUtils.getTotalSessionCount(aUser.split(",")(SessionizedData.sessionizedData.index)))
    sessionTimes.foreach(t => totalSessiontime += (t / 1000)) // convert to second
    sessionCounts.foreach(c => totalSessionCount += c)
    val avgSessionTime = totalSessiontime.value.toDouble / totalSessionCount.value.toDouble
    sc.parallelize(Array("Total session time: " + totalSessiontime.value + "seconds",
      "Total session count: " + totalSessionCount.value,
      "Avg session time: " + avgSessionTime + "seconds"))
      .repartition(1).saveAsTextFile(outputFilePath + avgSessionTimeFileName) // output avg session result

    /*
     * Total session time and count by each user.
     * Sort by total session count.
     * (IP, totalSessionTime, totalSessionCount)
     */
    val totalSessionTimeAndCountPerUser = userSessions.map(aUser => {
      aUser.split(",")(SessionizedData.ip.index) + "," +
        DataParsingUtils.getTotalSessionTime(aUser.split(",")(SessionizedData.sessionizedData.index)) + "," +
        DataParsingUtils.getTotalSessionCount(aUser.split(",")(SessionizedData.sessionizedData.index))
    })
    val sortByTotalSessionCount = totalSessionTimeAndCountPerUser.sortBy(_.split(",")(TotalSessionTimeAndCountPerUser.count.index), false)
    sortByTotalSessionCount.repartition(1).saveAsTextFile(outputFilePath + totalSessionTimeAndCountPerUserFileName)
  }

  implicit def rddToPairRDDFunctions[K: ClassTag, V: ClassTag](rdd: RDD[(K, V)]) =
    new PairRDDFunctions(rdd)
}
