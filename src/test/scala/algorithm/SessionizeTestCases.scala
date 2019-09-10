package algorithm

import org.scalatest.FlatSpec

class SessionizeTestCases extends FlatSpec {
  "The size of list appended with element" should "be > 0" in {
  var it = List.empty[String]
    it :+= "test1"
    System.out.println(it)
    it :+= "test2"
    System.out.println(it)
    System.out.println(it.mkString("#"))

    assert(it.size > 0)

}

}
