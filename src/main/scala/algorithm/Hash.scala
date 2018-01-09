package algorithm


import com.google.common.hash.Hashing

import scala.math.BigInt

/**
  * Created by lee on 17-3-22.
  */
trait Hash {
  private final val hash = Hashing.goodFastHash(64)

  private[algorithm] def hash(value: Any): BigInt = {
    val hashCode = hash.newHasher.putBytes(value.toString.getBytes).hash.asBytes
    BigInt(hashCode)
  }
}
