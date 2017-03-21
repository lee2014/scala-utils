package algorithm

import data.{BitMap, Serialization}

import java.security.MessageDigest
import scala.math._


/**
  * Description: https://en.wikipedia.org/wiki/Flajolet%E2%80%93Martin_algorithm
  * Created by lee on 17-3-20.
  */
class FlajoletMartin(length: Int = 512) {

  final val bitmap = BitMap(length)

  final val factor = 0.77351

  def md5(bytes: Array[Byte]): Array[Byte] = {
    MessageDigest.getInstance("MD5").digest(bytes)
  }

  def leastSignificant(value: Any): Int = {
    val hash = md5(Serialization.serialize(value).reverse)

    hash.zipWithIndex.foreach { item =>
      val (byte, index) = item

      BitMap.Bytes.indices.foreach { i =>
        if ((BitMap.Bytes(i) & byte) != 0) return index * 8 + i
      }
    }

    bitmap.length
  }

  def set(value: Any): Unit = {
    val index = leastSignificant(value)
    bitmap.set(index)
  }

  def distinct(): Int = {
    var R: Int = bitmap.length

    Range(0, bitmap.length).foreach { i =>
      if (!bitmap.get(i)) R = i
    }

    (pow(2, R) / factor).toInt
  }

}
