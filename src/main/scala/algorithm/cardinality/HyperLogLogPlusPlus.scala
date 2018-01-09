package algorithm.cardinality

import scala.math._

/**
  * The description of this algorithm:
  *   http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf
  *   https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/40671.pdf
  *
  * Created by lee on 17-3-21.
  */
class HyperLogLogPlusPlus(private[algorithm] override val p: Int) extends LogLog(p: Int) {

  /**
    * A Linear-Time Probabilistic Counting
    * The Description: http://organ.kaist.ac.kr/Prof/pdf/Whang1990(linear).pdf
    */
  def linearCounting(v: Int): Double = m * log(m.toDouble / v)

  /**
    * The constant alpha is provided by the analysis of
    * [HyperLogLog: the analysis of a near-optimal cardinality estimation algorithm]
    */
  private[algorithm] override def alpha: Double = 1 / (2 * log(2) * (1 + (3 * log(2) - 1) / m))

  /**
    * Obviously, m * z is the harmonic mean of all register's counter.
    * So m * m * z is close to the cardinality n of multiSet
    */
  private[algorithm] override def rawEstimate: Double = {
    val z = registers.map(x => pow(2, -x)).sum
    alpha * pow(m, 2) * pow(z, -1)
  }

  /**
    * Return the approximate count of the cardinality n
    */
  override def estimate: Long = {
    var e: Double = rawEstimate

    if ( e <= 5 * m / 2) {
      val zeros = registers.count(_ == 0)
      if (zeros != 0) e = linearCounting(zeros)
    } else if (e > (pow(2, 32) / 30)) {
      e = - pow(2, 32) * log(1 - e / pow(2, 32))
    }
    e.toLong
  }

  def +(other: HyperLogLogPlusPlus): HyperLogLogPlusPlus =
    merge(other.asInstanceOf[LogLog]).asInstanceOf[HyperLogLogPlusPlus]
}

object HyperLogLogPlusPlus {
  final val alphas: Array[Double] = Array[Double](0.673, 0.697, 0.709)

  def apply(p: Int = 4): HyperLogLogPlusPlus = new HyperLogLogPlusPlus(p)
}
