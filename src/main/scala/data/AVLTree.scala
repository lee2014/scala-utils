package data

import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

/**
  * Created By chengli at 03/01/2018
  */
class AVLTree[T <: Comparable[T]] {
  private final val NULL = null.asInstanceOf[T]

  private val root: AVLNode[T] = new AVLNode[T](NULL)

  private val rwLock: ReadWriteLock = new ReentrantReadWriteLock

  /**
    * ReadLock function template
    * @param f the function need readLock
    * @tparam R the return type of function "f"
    * @return
    */
  private def read[R](f: => R): R = {
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
  private def write[R](f: => R): R = {
    rwLock.writeLock.lock()
    try {
      f
    } finally {
      rwLock.writeLock.unlock()
    }
  }

  /**
    * rotate left left unbalance avl tree
    * @param node the node to be rotated
    * @return the root of rotated tree
    */
  private def ll_rotate(node: AVLNode[T]): AVLNode[T] = {
    val leftChild = node.left
    node.left = leftChild.right
    leftChild.right = node
    calHeight(node)
    calHeight(leftChild)
    leftChild
  }

  /**
    * rotate right right unbalance avl tree
    * @param node node the node to be rotated
    * @return the root of rotated tree
    */
  private def rr_rotate(node: AVLNode[T]): AVLNode[T] = {
    val rightChild = node.right
    node.right = rightChild.left
    rightChild.left = node
    calHeight(node)
    calHeight(rightChild)
    rightChild
  }

  /**
    * rotate left right unbalance avl tree
    * @param node node the node to be rotated
    * @return the root of rotated tree
    */
  private def lr_rotate(node: AVLNode[T]): AVLNode[T] = {
    val leftChild = node.left
    node.left = rr_rotate(leftChild)
    ll_rotate(node)
  }

  /**
    * rotate right left unbalance avl tree
    * @param node node the node to be rotated
    * @return the root of rotated tree
    */
  private def rl_rotate(node: AVLNode[T]): AVLNode[T] = {
    val rightChild = node.right
    node.right = ll_rotate(rightChild)
    rr_rotate(node)
  }

  /**
    * Get the height of a node
    * @param node AVLNode
    * @return height of a node in AVLTree
    */
  private def getHeight(node: AVLNode[T]): Int = {
    if (node == null) 0 else node.getHeight
  }

  /**
    * Recalculate the height of a node in AVLTree
    * @param node AVLNode
    */
  private def calHeight(node: AVLNode[T]): Unit = {
    node.setHeight(math.max(getHeight(node.left), getHeight(node.right)) + 1)
  }

  /**
    * Get the balance factor of a node in AVLTree
    * @param node AVLNode
    * @return the balance factor
    */
  private def getBalance(node: AVLNode[T]): Int = {
    if (node == null) 0 else getHeight(node.left) - getHeight(node.right)
  }

  /**
    * Re-balance an AVLTree
    * @param node AVLNode
    * @return the root of a re-balanced AVLTree
    */
  private def balance(node: AVLNode[T]): AVLNode[T] = {
    val balance = getBalance(node)

    // left left slope
    if (balance > 1 && getBalance(node.left) > 0) return ll_rotate(node)

    // right right slope
    if (balance < -1 && getBalance(node.right) < 0) return rr_rotate(node)

    // left right slope
    if (balance > 1 && getBalance(node.left) < 0) return lr_rotate(node)

    // right left slope
    if (balance < -1 && getBalance(node.right) > 0) return rl_rotate(node)

    node
  }

  /**
    * Insert a value to AVLTree
    * @param value to be inserted
    * @param node AVLNode
    * @return the root of a new AVLTree
    */
  private def _insert(value: T, node: AVLNode[T]): AVLNode[T] = {
    if (node == null) return AVLNode(value)

    if (node.getValue.compareTo(value) > 0) {
      node.left = _insert(value, node.left)
    } else if (node.getValue.compareTo(value) < 0) {
      node.right = _insert(value, node.right)
    } else {
      return node
    }

    calHeight(node)
    balance(node)
  }

  /** Insert a value to AVLTree */
  final def insert(value: T): Unit = write {
    root.left = _insert(value, root.left)
  }

  /** Find a value whether in AVLTree */
  private def _find(value: T, node: AVLNode[T]): AVLNode[T] = {
    if (node == null) null
    else if (value.compareTo(node.getValue) < 0) _find(value, node.left)
    else if (value.compareTo(node.getValue) > 0) _find(value, node.right)
    else node
  }

  /** Find a value whether in AVLTree */
  final def find(value: T): Option[T] = read {
    Option[AVLNode[T]](_find(value, root.left)).map(_.getValue)
  }

  /** Erase a value from AVLTree */
  private def _erase(value: T, node: AVLNode[T]): AVLNode[T] = {
    var newNode: AVLNode[T] = node
    
    if (newNode == null) return null

    if (value.compareTo(newNode.getValue) < 0) newNode.left = _erase(value, newNode.left)
    else if (value.compareTo(newNode.getValue) > 0) newNode.right = _erase(value, newNode.right)
    else {
      if (newNode.left != null && newNode.right != null) {
        var right = newNode.right
        while (right.left != null) right = right.left
        newNode.setValue(right.getValue)
        newNode.right = _erase(right.getValue, newNode.right)
      } else {
        newNode = if (newNode.left != null) newNode.left else newNode.right
        if (newNode == null) return null
      }
    }

    calHeight(newNode)

    val balance = getBalance(newNode)

    // left left slope
    if (balance > 1 && getBalance(newNode.left) >= 0) return ll_rotate(newNode)

    // right right slope
    if (balance < -1 && getBalance(newNode.right) <= 0) return rr_rotate(newNode)

    // left right slope
    if (balance > 1 && getBalance(newNode.left) < 0) return lr_rotate(newNode)

    // right left slope
    if (balance < -1 && getBalance(newNode.right) > 0) return rl_rotate(newNode)

    newNode
  }

  /** Erase a value from AVLTree */
  final def erase(value: T): Unit = write {
    root.left = _erase(value, root.left)
  }

  /** MidSearch a AVLTree */
  final def midSearch(f: T => Unit): Unit = read {
    midSearch(root.left)(f)
  }
  
  private def midSearch(node: AVLNode[T])(f: T => Unit): Unit = {
    if (node != null) {
      midSearch(node.left)(f)
      f(node.getValue)
      midSearch(node.right)(f)
    }
  }

  /** PreSearch a AVLTree */
  final def preSearch(f: T => Unit): Unit = read {
    preSearch(root.left)(f)
  }

  private def preSearch(node: AVLNode[T])(f: T => Unit): Unit = {
    if (node != null) {
      f(node.getValue)
      preSearch(node.left)(f)
      preSearch(node.right)(f)
    }
  }
}

private[AVLTree] class AVLNode[T <: Comparable[T]](private var value: T) extends BSNode[T, AVLNode](value) {
  private var height: Int = 1

  def setHeight(h: Int): Unit = this.height = h

  def getHeight: Int = this.height
}

object AVLNode {
  def apply[T <: Comparable[T]](value: T): AVLNode[T] = new AVLNode[T](value)
}

