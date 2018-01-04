package data

import org.scalatest.{BeforeAndAfterAll, FunSuite}

class AVLTest extends FunSuite with BeforeAndAfterAll {
  test("test AVLTree[Integer]") {
    val tree = new AVLTree[Integer]

    Range(0, 5).reverse.foreach(i => tree.insert(i))

    println(">>>>>>>>>MidSearch<<<<<<<<<<<<")
    tree.midSearch(println)
    println(">>>>>>>>>PreSearch<<<<<<<<<<<<")
    tree.preSearch(println)
    println(">>>>>>>>>findOne<<<<<<<<<<<<<<")
    println(tree.find(2))
    println(">>>>>>>>>findOne<<<<<<<<<<<<<<")
    println(tree.find(5))
    println(">>>>>>>>>eraseOne<<<<<<<<<<<<<")
    tree.erase(3)
    tree.preSearch(println)
  }

  test("test AVLTree[String]") {
    val tree = new AVLTree[String]

    Seq("China", "USA", "UK", "India", "Russia", "Korea", "Japan").foreach(tree.insert)

    println(">>>>>>>>>MidSearch<<<<<<<<<<<<")
    tree.midSearch(println)
    println(">>>>>>>>>PreSearch<<<<<<<<<<<<")
    tree.preSearch(println)
  }

  test("test insert duplicate value") {
    val tree = new AVLTree[Integer]

    Range(0, 10).foreach(_ => tree.insert(1))

    println(">>>>>>>>>PreSearch<<<<<<<<<<<<")
    tree.preSearch(println)
  }
}
