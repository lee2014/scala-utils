package algorithm.cardinality

import algorithm.MD5Hash

import scala.math._

/**
  * Description: https://en.wikipedia.org/wiki/Flajolet%E2%80%93Martin_algorithm
  * Created by lee on 17-3-20.
  */
class FlajoletMartin(private val p: Int, private val L: Int)
  extends CardinalityEstimation with MD5Hash {

  require(p > 0 && L > 0)

  private[algorithm] val m: Int = pow(2, p).toInt

  private final val bitmaps = Array.fill[BigInt](m)(BigInt(0))

  private final val factor = 0.77351

  private[algorithm] def rho(value: BigInt): Int = value.lowestSetBit

  private[algorithm] override def insert(value: Any): Unit = {
    val hashValue = hash(value).abs
    val mapIdx = (hashValue % m).toInt
    val index = rho(hashValue / m)

    bitmaps(mapIdx) = bitmaps(mapIdx) | BigInt(pow(2, index).toLong)
  }

  /**
    * E := nmap / (q * (1 + 0.31 / nmap ))*2**(S/nmap)
    */
  override def estimate: Long = {
    var S: Double = 0

    for (i <- 0 until m) {
      var R = 0
      while (R <= i && bitmaps(i).testBit(R) && R < L) R += 1
      S += R
    }

    (m * pow(2, S / m) / (factor * (1 + 0.31 / m))).toLong
  }
}

object FlajoletMartin {
  def apply(n: Int, l: Int): FlajoletMartin = new FlajoletMartin(n, l)
}
