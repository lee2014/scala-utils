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
    println(s"Error: ${error * 100}%")
    println(s"Expectation: ${0.78 / sqrt(flajoletMartin.m) * 100}%")
  }

  test("test HyperLogLogPlusPlus") {
    val hyperLogLog = HyperLogLogPlusPlus(18)

    val random = new Random(System.nanoTime)
    val tests = Range(0, 10000000).map(_ => random.nextInt(200000))

    printlnRunTime { hyperLogLog.insertAll(tests) }
    val probabilityCount = hyperLogLog.estimate
    val realCount = tests.distinct.size

    val error = (probabilityCount.toDouble - realCount).abs / realCount

    println(s"ProbabilityCount: $probabilityCount")
    println(s"RealCount: $realCount")
    println(s"Error: ${error * 100}%")
    println(s"Expectation: ${1.04 / sqrt(hyperLogLog.m) * 100}%")
  }

  def printlnRunTime[T](f: => T): T = {
    val before = System.currentTimeMillis()
    val result = f
    val after = System.currentTimeMillis()
    println(after - before)
    result
  }

  test("test LogLog") {
    val random = new Random(System.nanoTime)

    val tests = printlnRunTime {
      Range(0, 1000).par.map { iter =>
        Range(0, 10000).map(_ => random.nextInt(200000))
      }.seq
    }

    val loglog = printlnRunTime {
      val ll = LogLog(16)
      ll.insertAll(tests.flatten)
      ll
    }

    val probabilityCount = loglog.estimate
    val realCount = printlnRunTime {
      tests.flatten.distinct.size
    }

    val error = (probabilityCount.toDouble - realCount).abs / realCount

    println(s"Error: ${error * 100}%")
    println(s"Expectation: ${130 / sqrt(loglog.m)}%")
  }
}
