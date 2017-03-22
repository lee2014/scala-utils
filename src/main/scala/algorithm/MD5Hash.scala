package algorithm

import java.security.MessageDigest

import data.Serialization

import scala.math.BigInt

/**
  * Created by lee on 17-3-22.
  */
trait MD5Hash {
  private[algorithm] def hash(value: Any): BigInt = {
    val bytes = MessageDigest.getInstance("MD5").digest(Serialization.serialize(value))
    BigInt(bytes)
  }
}
