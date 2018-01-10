package data

import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

import scala.language.higherKinds

/**
  * Binary Search Tree
  * Created By chengli at 10/01/2018
  */
abstract class BSTree[T <: Comparable[T]] {

  private val rwLock: ReadWriteLock = new ReentrantReadWriteLock

  /**
    * ReadLock function template
    * @param f the function need readLock
    * @tparam R the return type of function "f"
    * @return
    */
  protected def read[R](f: => R): R = {
    rwLock.readLock.lock()
    try {
      f
    } finally {
      rwLock.readLock.unlock()
    }
  }

  /**
    * WriteLock function template
    * @param f the function need writeLock
    * @tparam R the return type of function "f"
    * @return
    */
  protected def write[R](f: => R): R = {
    rwLock.writeLock.lock()
    try {
      f
    } finally {
      rwLock.writeLock.unlock()
    }
  }

  def find(value: T): Option[T]
}

/**
  * Binary Search Node
  * @param value the key of a node
  * @tparam T the Class Type of Key
  * @tparam Node the Class Type of Node
  */
abstract class BSNode[T <: Comparable[T], Node[_ <: Comparable[T]]](private var value: T)
  extends Comparable[BSNode[T, Node]] {

  private[data] var left: Node[T] = _

  private[data] var right: Node[T] = _

  private[data] def setValue(value: T): Unit = this.value = value

  private[data] def getValue: T = value

  override def compareTo(o: BSNode[T, Node]): Int = {
    value.compareTo(o.getValue)
  }
}