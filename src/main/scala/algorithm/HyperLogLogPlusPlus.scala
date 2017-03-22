package algorithm

import util.Util._

import scala.math._
import scala.util.control.Breaks._


/**
  * The description of this algorithm:
  *   http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf
  *   https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/40671.pdf
  *
  * Created by lee on 17-3-21.
  */
class HyperLogLogPlusPlus(private val p: Int) extends CardinalityEstimation with MD5Hash {

  require(4 <= p && p <= 16,
    s"Parameter p: $p shouldn't be out of range from 4 to 16!")

  private[algorithm] final val m: Int = pow(2, p).toInt
  private[algorithm] final val registers: Array[Int] = Array.fill(m)(0)

  /**
    * Find registerIndex from hashed value.
    *
    * Suppose x is hashed value of the input, x(i) is i-th bit of x,
    * [1100]2 is the binary representation of 12.
    * So we define registerIndex := [x(63), . . . , x64−p]2
    */
  private[algorithm] def registerIndex(value: BigInt): Int = {
    ((value >> (64 - p)).abs % m).toInt
  }

  /**
    * Add an element
    * @param value An element of MultiSet
    */
  private[algorithm] override def insert(value: Any): Unit = {
    val x: BigInt = hash(value)
    val idx: Int = registerIndex(x)
    registers(idx) = max(registers(idx), rho(x))
  }

  /**
    * A Linear-Time Probabilistic Counting
    * The Description: http://organ.kaist.ac.kr/Prof/pdf/Whang1990(linear).pdf
    */
  def linearCounting(v: Int): Long = (m * log(m.toDouble / v)).toLong

  /**
    * The constant alpha is provided by the analysis of
    * [HyperLogLog: the analysis of a near-optimal cardinality estimation algorithm]
    */
  private[algorithm] def alpha: Double = {
    if (p <= 6) HyperLogLogPlusPlus.alphas(p - 4)
    else 0.7213 / ( 1 + 1.079 / m)
  }

  /**
    * ρ(s) represent the position of the leftmost 1.
    * For examples, ρ([0001...]2) = 4, ρ([001...]2) = 3, ρ([1...]2) = 1
    */
  private[algorithm] def rho(value: BigInt): Int= {
    var rhoValue = 64 - p - 1

    breakable {
      Range(0, 64 - p).foreach { index =>
        if (value.testBit(index)) {
          rhoValue = index
          break
        }
      }
    }

    rhoValue + 1
  }

  /**
    * Obviously, m * z is the harmonic mean of all register's counter.
    * So m * m * z is close to the cardinality n of multiSet
    */
  private[algorithm] def rawEstimate: Long = {
    val z = registers.map(x => pow(2, -x)).sum
    (alpha * pow(m, 2) * pow(z, -1)).toLong
  }

  /**
    * Return the approximate count of the cardinality n
    */
  def estimate: Long = {
    var e: Long = rawEstimate

    if ( e <= 5 * m / 2) {
      val zeros = registers.count(_ == 0)
      if (zeros != 0) e = linearCounting(zeros)
    } else if (e > (pow(2, 32) / 30)) {
      e = (- pow(2, 32) * log(1 - e / pow(2, 32))).toLong
    }
    e
  }
}

object HyperLogLogPlusPlus {
  final val alphas: Array[Double] = Array[Double](0.673, 0.697, 0.709)

  def apply(p: Int = 4): HyperLogLogPlusPlus = new HyperLogLogPlusPlus(p)
}
