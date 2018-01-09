package algorithm.cardinality

import algorithm.Hash
import util.Util.require

import scala.math.{BigInt, Pi, log, max, pow}
import scala.util.control.Breaks.{break, breakable}

/**
  * Created by lee on 17-3-28.
  */
class LogLog(private[algorithm] val p: Int) extends CardinalityEstimation with Hash {
  require(4 <= p && p <= 20,
    s"Parameter p: $p shouldn't be out of range from 4 to 16!")

  val lnOf2: Double = log(2) // natural log of 2
  def log2(x: Double): Double = log(x) / lnOf2

  private[algorithm] final val m: Int = pow(2, p).toInt
  private[algorithm] final val registers: Array[Int] = Array.fill(m)(0)

  /**
    * Find registerIndex from hashed value.
    *
    * Suppose x is hashed value of the input, x(i) is i-th bit of x,
    * [1100]2 is the binary representation of 12.
    * So we define registerIndex := [x(63), . . . , x(64−p)]2
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
    val w: Int = rho(x)

    if (w > registers(idx)) registers(idx) = w
  }


  /**
    * Description: Loglog Counting of Large Cardinalities
    * αm ∼ α∞ − (2π2 + log2 2)/(48m), where α∞ = e−γ√2/2 = 0.39701 (γ is Euler’s constant)
    */
  private[algorithm] def alpha: Double =
    0.39701 - (2 * pow(Pi, 2) + pow(log2(2), 2)) / (48 * m)

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

  private[algorithm] def rawEstimate: Double = {
    val z = registers.map(_.toDouble).sum / m
    alpha * m * pow(2, z)
  }

  /**
    * Return the approximate count of the cardinality n
    */
  def estimate: Long = rawEstimate.toLong

  private[algorithm] def merge(other: LogLog): LogLog = {
    if (m != other.m) throw new IllegalArgumentException("Can't be added together!")
    Range(0, m).foreach(i => registers(i) = max(registers(i), other.registers(i)))
    this
  }

  def +(other: LogLog): LogLog = merge(other)
}

object LogLog {
  def apply(p: Int = 4): LogLog = new LogLog(p)
}
