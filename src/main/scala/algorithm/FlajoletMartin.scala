package algorithm

import scala.math._
import scala.util.control.Breaks._

/**
  * Description: https://en.wikipedia.org/wiki/Flajolet%E2%80%93Martin_algorithm
  * Created by lee on 17-3-20.
  */
class FlajoletMartin extends CardinalityEstimation with MD5Hash {

  private final var bitmap = BigInt(0)

  private final val factor = 0.77351

  private[algorithm] def leastSignificant(value: Any): Int = hash(value).lowestSetBit

  private[algorithm] override def insert(value: Any): Unit = {
    bitmap = bitmap | BigInt(pow(2, leastSignificant(value)).toLong)
  }

  override def estimate: Long = {
    var R: Int = bitmap.bitLength

    breakable {
      Range(0, bitmap.bitLength).foreach { i =>
        if (!bitmap.testBit(i)) {
          R = i
          break
        }
      }
    }

    (pow(2, R) / factor).toLong
  }
}
