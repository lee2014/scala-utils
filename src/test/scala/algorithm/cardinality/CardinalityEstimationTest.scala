package algorithm.cardinality

import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.math.{abs, sqrt}
import scala.util.Random

/**
  * Created by lee on 17-3-29.
  */
class CardinalityEstimationTest extends FunSuite with BeforeAndAfterAll{
  test("test FlajoletMartin") {
    val flajoletMartin = FlajoletMartin(10, 64)

    val random = new Random(System.nanoTime)
    val tests = Range(0, 10000000).map(_ => random.nextInt(200000))

    flajoletMartin.insertAll(tests)
    val probabilityCount = flajoletMartin.estimate
    val realCount = tests.distinct.size

    val error = abs(probabilityCount.toDouble - realCount) / realCount

    println(probabilityCount)
    println(s"Error: $error")
    println(s"Expectation: ${0.78 / sqrt(flajoletMartin.m)}")
  }

  test("test HyperLogLogPlusPlus") {
    val hyperLogLog = HyperLogLogPlusPlus(16)

    val random = new Random(System.nanoTime)
    val tests = Range(0, 10000000).map(_ => random.nextInt(200000))

    hyperLogLog.insertAll(tests)
    val probabilityCount = hyperLogLog.estimate
    val realCount = tests.distinct.size

    val error = (probabilityCount.toDouble - realCount) / realCount

    println(s"Error: $error")
    println(s"Expectation: ${1.04 / sqrt(hyperLogLog.m)}")
  }

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
