package data

import scala.language.higherKinds

/**
  * Binary Search Tree
  * Created By chengli at 10/01/2018
  */
abstract class BSTree[T <: Comparable[T]] {
}

/**
  * Binary Search Node
  * @param value the key of a node
  * @tparam T the Class Type of Key
  * @tparam Node the Class Type of Node
  */
abstract class BSNode[T <: Comparable[T], Node[_ <: Comparable[T]]](private var value: T) extends Comparable[BSNode[T, Node]] {
  private[data] var left: Node[T] = _

  private[data] var right: Node[T] = _

  private[data] def setValue(value: T): Unit = this.value = value

  private[data] def getValue: T = value

  override def compareTo(o: BSNode[T, Node]): Int = {
    value.compareTo(o.getValue)
  }
}