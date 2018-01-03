package algorithm.regexgen

import scala.collection.mutable.ArrayBuffer

/**
  * Created by chengli at 06/09/2017
  */
abstract class ExtractDiscovery {
  def extract(events: ArrayBuffer[String]): Unit

  def genPatterns(events: ArrayBuffer[String], values: ArrayBuffer[String]): ArrayBuffer[String]

  def genPattern(event: String, value: String): String

  def genPrefixRegex(prefix: String): String

  def genRegex(value: String): String = {
    val regex = StringBuilder.newBuilder
    var previous: Char = null

    value.indices.foreach { idx =>
      if (Character.isLetter(value(idx)) || Character.isDigit(value(idx))) {
      }
    }

    regex.mkString
  }
}