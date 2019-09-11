package app

import algorithm.Sessionize
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

    /*-------- Columns schema --------*/
    case class ColumnsDefinition(columnsName: String, index: Int)
    /* Rawdata columns definition */
      val clientIpWithPort = ColumnsDefinition("clientIpWithPort", 2)
      val time = ColumnsDefinition("time", 0)
      val request = ColumnsDefinition("request", 12)

    /* Clean data column definition */
      /* sessionized data */
      val ip = ColumnsDefinition("clientIp", 0)
      val sessionizedData = ColumnsDefinition("sessionizedData", 1)
      /* totalSessionTimeAndCountPerUser */
      object TotalSessionTimeAndCountPerUser {
        val ip = ColumnsDefinition("clientIp", 0)
        val totalSessionTime = ColumnsDefinition("totalSessionTime", 1)
        val totalSessionCount = ColumnsDefinition("totalSessionCount", 2)
      }

    /*-------- Columns schema --------*/

    val conf = new SparkConf()
    val sc = new SparkContext()

    val data = sc.textFile(inputFilePath)

    // group by user identity. Use Ip address as id. (remove port)
    // (client_IP, time, request)
    val filterColumns = data.map(line => ((StringUtils.removePort(line.split(" ")(clientIpWithPort.index)), line.split(" ")(time.index)), line.split(" ")(request.index)))

    // Sortby both Ip and and time
    val sortByUserAndTime = filterColumns.sortBy(_._1)
    sortByUserAndTime.saveAsTextFile(outputFilePath + sortedByIpAndTimeFileName)

    // (client_IP, Iterable(client_IP:port, time, request))
    val groupByUser = sortByUserAndTime.map(r => (r._1._1, r._1._2, r._2)).groupBy(r => r._1)

    // (IP, sessionizedData)
    val userWithSession = groupByUser.map(aUser => {
      aUser._1 + "," + Sessionize.getSessionizedUserdata(aUser._2)
    })
    userWithSession.repartition(1).saveAsTextFile(outputFilePath + sessionizedDataFileName) // output the sessionized data

    // Find average session time
    val totalSessiontime = sc.accumulator(0)
    val totalSessionCount = sc.accumulator(0)
    val sessionTimes = userWithSession.map(aUser => DataParsingUtils.getTotalSessionTime(aUser.split(",")(sessionizedData.index)))
    val sessionCounts = userWithSession.map(aUser => DataParsingUtils.getTotalSessionCount(aUser.split(",")(sessionizedData.index)))
    sessionTimes.foreach(t => totalSessiontime += (t / 1000)) // convert to second
    sessionCounts.foreach(c => totalSessionCount += c)
    val avgSessionTime = totalSessiontime.value.toDouble / totalSessionCount.value.toDouble
    sc.parallelize(Array("Total session time: " + totalSessiontime.value, "Total session count: " + totalSessionCount.value, "Avg session time: " + avgSessionTime)).repartition(1).saveAsTextFile(outputFilePath + avgSessionTimeFileName) // output avg session result

    val totalSessionTimeAndCountPerUser = userWithSession.map(aUser => aUser.split(",")(ip.index) + "," + DataParsingUtils.getTotalSessionTime(aUser.split(",")(sessionizedData.index)) + "," + DataParsingUtils.getTotalSessionCount(aUser.split(",")(sessionizedData.index)))
    val sortByTotalSessionCount = totalSessionTimeAndCountPerUser.sortBy(_.split(",")(TotalSessionTimeAndCountPerUser.totalSessionCount.index), false)
    sortByTotalSessionCount.repartition(1).saveAsTextFile(outputFilePath + totalSessionTimeAndCountPerUserFileName)
  }

  implicit def rddToPairRDDFunctions[K: ClassTag, V: ClassTag](rdd: RDD[(K, V)]) =
    new PairRDDFunctions(rdd)
}
