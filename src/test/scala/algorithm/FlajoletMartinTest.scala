package algorithm

import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.math._
import scala.util.Random

/**
  * Created by lee on 17-3-22.
  */
class FlajoletMartinTest extends FunSuite with BeforeAndAfterAll {
  test("test base FlajoletMartin") {
    val flajoletMartin = new FlajoletMartin

    val random = new Random(System.nanoTime)
    val tests = Range(0, 1000000)

    tests.map(flajoletMartin.leastSignificant)
      .map(x => (x, 1))
      .groupBy(_._1)
      .map(tuple => (tuple._1, tuple._2.map(_._2).sum))
      .foreach(println)

    flajoletMartin.insertAll(tests)
    val probabilityCount = flajoletMartin.estimate
    val realCount = tests.distinct.size

    val error = abs(probabilityCount.toDouble - realCount) / realCount

    println(probabilityCount)
    println(s"Error: $error")
  }
}
