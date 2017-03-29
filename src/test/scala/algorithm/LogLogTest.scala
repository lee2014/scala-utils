package algorithm

import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.math.sqrt
import scala.util.Random

/**
  * Created by lee on 17-3-28.
  */
class LogLogTest extends FunSuite with BeforeAndAfterAll {
  test("test LogLog") {
    val loglog = LogLog(16)

    val random = new Random(System.nanoTime)
    val tests = Range(0, 10000000).map(_ => random.nextInt(200000))

    loglog.insertAll(tests)
    val probabilityCount = loglog.estimate
    val realCount = tests.distinct.size

    val error = (probabilityCount.toDouble - realCount) / realCount

    println(s"Error: $error")
    println(s"Expectation: ${1.3 / sqrt(loglog.m)}")
  }
}
