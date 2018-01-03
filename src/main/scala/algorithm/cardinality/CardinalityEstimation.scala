package algorithm.cardinality

/**
  * Created by lee on 17-3-22.
  */
trait CardinalityEstimation {
  private[algorithm] def insert(value: Any): Unit

  def insertAll(values: Seq[Any]): Unit = values.foreach(insert)

  def estimate: Long
}
