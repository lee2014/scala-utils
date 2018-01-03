package algorithm

import java.util

import org.nd4j.linalg.factory.Nd4j
import org.sameersingh.scalaplot.XYPlotStyle
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.tensorflow.{Graph, Session, Tensor, TensorFlow}

/**
  * Created by chengli on 16/06/2017.
  */
class MLTest extends FunSuite with BeforeAndAfterAll {
  test("hello tensorflow") {
    val graph = new Graph()
    val value = "Hello from " + TensorFlow.version()

    // Construct the computation graph with a single operation, a constant
    // named "MyConst" with a value "value".
    val tensor = Tensor.create(value.getBytes("UTF-8"))
    // The Java API doesn't yet include convenience functions for adding operations.
    graph.opBuilder("Const", "MyConst").setAttr("dtype", tensor.dataType).setAttr("value", tensor).build

    // Execute the "MyConst" operation in a Session.
    val session = new Session(graph)
    val output = session.runner().fetch("MyConst").run().get(0)
    System.out.println(new String(output.bytesValue(), "UTF-8"))
  }
  
  test("prediction") {
    val graph = new Graph()
    val session = new Session(graph)

    var x = Tensor.create(2.0f)
    var y = session.runner().feed("x", x).fetch("y").run().get(0)
    System.out.println(y.floatValue())  // Will print 6.0f
    x = Tensor.create(1.1f)
    y = session.runner().feed("x", x).fetch("y").run().get(0)
    System.out.println(y.floatValue())
  }

  test("ND4J Test") {
    /** Creating arrays in multiple ways, all using numpy syntax */

    val arr = Nd4j.create(4)
    val arr2 = Nd4j.ones(4)
    val arr3 = Nd4j.linspace(1, 10, 10)
    val arr4 = Nd4j.linspace(1, 6, 6).reshape(2, 3)

    /** Array addition in place */
    arr.add(arr2)
    arr.add(2)

    /** Array multiplication in place */
    arr2.mul(5)

    /** Transpose matrix */

    val arrT = arr.transpose()

    /** Row (0) and Column (1) Sums */

    println(Nd4j.sum(arr4, 0) + "Calculate the sum for each row")
    println(Nd4j.sum(arr4, 1) + "Calculate the sum for each column")

    /** Checking array shape */

    println(util.Arrays.toString(arr2.shape) + "Checking array shape")

    /** Converting array to a string */

    println(arr2.toString + "Array converted to string")

    /** Filling the array with the value 5 (same as numpy's fill method) */

    println(arr2.assign(5) + "Array assigned value of 5 (equivalent to fill method in numpy)")

    /** Reshaping the array */

    println(arr2.reshape(2, 2) + "Reshaping array")

    /** Raveling the array (returns a flattened array) */

    println(arr2.ravel + "Raveling array")

    /** Flattening the array (same as numpy's flatten method) */

    println(Nd4j.toFlattened(arr2) + "Flattening array (equivalent to flatten in numpy)")

    /** Array sorting */

    println(Nd4j.sort(arr2, 0, true) + "Sorting array")
    println(Nd4j.sortWithIndices(arr2, 0, true) + "Sorting array and returning sorted indices")

    /** Cumulative sum */

    println(Nd4j.cumsum(arr2) + "Cumulative sum")

    /** Basic stats methods */

    println(Nd4j.mean(arr) + "Calculate mean of array")
    println(Nd4j.std(arr2) + "Calculate standard deviation of array")
    println(Nd4j.`var`(arr2), "Calculate variance")

    /** Find min and max values */

    println(Nd4j.max(arr3), "Find max value in array")
    println(Nd4j.min(arr3), "Find min value in array")
  }

  test("test"){
    import org.sameersingh.scalaplot.Implicits._

    val x = 0.0 until 10.0 by 0.01
    val rnd = new scala.util.Random(0)

    output(PNG("/Users/chengli/Downloads//", "scatter"), xyChart(
      x -> Seq(Y(x, style = XYPlotStyle.Lines),
        Y(x.map(_ + rnd.nextDouble - 0.5), style = XYPlotStyle.Dots))))

    val chart = xyChart(
      x -> Seq(Y(x, style = XYPlotStyle.Lines),
        Y(x.map(_ + rnd.nextDouble - 0.5), style = XYPlotStyle.Dots)))
  }
}
