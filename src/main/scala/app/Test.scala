package app

import algorithm.Sessionize
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd
import org.apache.spark.rdd.{PairRDDFunctions, RDD}

import scala.reflect.ClassTag

object Test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    val sc = new SparkContext()

    val data = sc.textFile(args(0))

    // group by user identity. Use Ip address and port as id.
    // (client_IP:port, time, backend_IP:port)
    val filtered = data.map(line => ((line.split(" ")(2).replace(line.split(" ")(2).substring(line.split(" ")(2).indexOf(":")), ""), line.split(" ")(0)), line.split(" ")(3)))

    // Sortby both Ip and and datetime
    val sortByUserAndTime = filtered.sortBy(_._1)

    // (client_IP:port, Iterable(client_IP:port, time, backend_IP:port))
    val groupByUser = sortByUserAndTime.map(r => (r._1._1, r._1._2, r._2)).groupBy(r => r._1)

    val userWithSession = groupByUser.map(aUser => {
      aUser._1 + "," + Sessionize.getSessionizedUserdata(aUser._2)
    })

    userWithSession.saveAsTextFile(args(1))
//    sortByUserAndTime.saveAsTextFile(args(1))

//    // (client_IP:port, (time, backend_IP:port))
//    val usersInfo = filtered.groupByKey()
//
//    usersInfo.sort
//    Iterable i =
//    usersInfo.map(aUser => {
//      aUser._2.sor
//    })
//
//    usersInfo.saveAsTextFile(args(1))
  }

  implicit def rddToPairRDDFunctions[K: ClassTag, V: ClassTag](rdd: RDD[(K, V)]) =
    new PairRDDFunctions(rdd)
}
