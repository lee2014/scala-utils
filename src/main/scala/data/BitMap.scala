package data

/**
  * Created by lee on 17-3-20.
  */

class BitMap(val bytes: Array[Byte]) extends IndexedSeq[(Int,Boolean)] {

  val length: Int = bytes.length * 8

  /**
    * Returns true if the bit at the given index is set, false if it is not.
    * @param index the bit position, starts at 0
    */

  def get(index: Int): Boolean = {
    val (quotient, remainder) = BitMap.position(index)

    if (index < 0 || index >= length)
      throw new IllegalArgumentException(s"The index($index) is out of range")
    (bytes(quotient) & BitMap.Bytes(remainder)) != 0
  }

  /**
    * Set the bit at the given index
    * @param index the bit position, starts at 0
    */
  def set(index: Int): Unit = {
    val (quotient, remainder) = BitMap.position(index)

    if (index < 0 || index >= length)
      throw new IllegalArgumentException(s"The index($index) is out of range")
    bytes(quotient) = (bytes(quotient) | BitMap.Bytes(remainder)).toByte
  }

  override def foreach[U](f: ((Int, Boolean)) => U) {
    var currentIndex = 0
    bytes.foreach { byte =>
      var x = 0
      while (x < BitMap.Bytes.length) {
        f(currentIndex, (byte & BitMap.Bytes(x)) != 0)
        x += 1
        currentIndex += 1
      }
    }
  }

  def apply(idx: Int): (Int, Boolean) = (idx, this.get(idx))

  override def toString: String = this.map(entry => if(entry._2) '1' else '0').mkString("")
}

object BitMap {
  final val Bytes = Array(1, 2, 4, 8, 16, 32, 64, 128)

  def position(index: Int): (Int, Int) = (index / 8, index % 8)

  def apply(bytes: Array[Byte]): BitMap = new BitMap(bytes)

  def apply(length: Int): BitMap = new BitMap(Array.fill[Byte](length / 8 + 1)(0.toByte))
}