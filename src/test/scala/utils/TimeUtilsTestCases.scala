package utils

import java.text.ParseException
import java.util.Date

import org.scalatest.FlatSpec

class TimeUtilsTestCases extends FlatSpec {
  "A Java Date" should "be represented" in {
    val d = TimeUtils.parse("2015-07-22T16:10:40.458786Z")
    val d2 = TimeUtils.parse("2015-07-22T16:10:40.458555Z")

    System.out.println(d2)
    assert(d.toDate.getTime > 0)
  }

//  it should "throw ParseException if the String cannot be parsed" in {
//    assertThrows[ParseException] {
//      val d = TimeUtils.parse("2015-07-22T16:10:40.458786Z")
//    }
//  }
}
