package algorithm


import com.google.common.hash.Hashing

import scala.math.BigInt

/**
  * Created by lee on 17-3-22.
  */
trait Hash {
  private final val hash = Hash.hash

  private[algorithm] def hash(value: Any): BigInt = {
    val hashCode = hash.hashBytes(value.toString.getBytes).asBytes
    BigInt(hashCode)
  }
}

object Hash {
  final val hash = Hashing.murmur3_128(System.currentTimeMillis().toInt)
}
