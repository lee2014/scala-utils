package data


/**
  * RedBlack Tree
  * Created By chengli at 10/01/2018
  */
class RBTree[T <: Comparable[T]] {

}

/**
  * RedBlack Tree Node
  * @param value the key of a node
  * @tparam T the Class Type of Key
  */
private[RBTree] class RBNode[T <: Comparable[T]](private var value: T) extends BSNode[T, RBNode](value) {
  private final val RED: String = "red"
  private final val BLACK: String = "black"

  private var color: String = _

  def getColor: String = this.color

  def setColor(c: String): Unit = this.color = c
}