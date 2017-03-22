package image

import java.awt.Color
import java.io._
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

import image.ImageUtils._
import util.Util._

import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by lee on 17-3-19.
  */
class ImageTest extends FunSuite with BeforeAndAfterAll {

  test("test load Image") {

    val newImage = ImageUtils.resizeImage(
      "/Users/chengli/Downloads/test/1.jpg",
      ImageType.IMAGE_JPEG,
      400, 400)

    val outFile = "/Users/chengli/Downloads/test_uniform/1.csv"
    val out = new FileOutputStream(outFile)
    val width = newImage.getWidth
    val height = newImage.getHeight

    val res = ArrayBuffer[String]()
    Range(0, height).foreach { y =>
      Range(0, width).foreach { x =>
        val rgb = newImage.getRGB(x, y)
        res += new Color(rgb).getRGB.toString
      }
    }

    val in = new ByteArrayInputStream(res.mkString(",").getBytes(StandardCharsets.UTF_8))
    copy(in, out)
    out.close()
  }

  def copy(input: InputStream, output: OutputStream): Unit = {
    val BUFFER_SIZE = 2 * 1024 * 1024

    try {
      val buffer = new Array[Byte](BUFFER_SIZE)
      var bytesRead = input.read(buffer)
      while (bytesRead != -1) {
        output.write(buffer, 0, bytesRead)
        bytesRead = input.read(buffer)
      }
    } finally {
      input.close()
      output.close()
    }
  }

  test("rgbImage2GreyImage") {
    val newImage = ImageUtils.resizeImage(
      "/Users/chengli/Downloads/test/1.jpg",
      ImageType.IMAGE_JPEG,
      50, 50)

    val greyImage = ImageUtils.rgbImage2GreyImage(newImage)
    ImageIO.write(greyImage, "jpg", new FileOutputStream("/Users/chengli/Downloads/test_uniform/1_grey.jpg"))
    ImageIO.write(newImage, "jpg", new FileOutputStream("/Users/chengli/Downloads/test_uniform/1_color.jpg"))
    val str = ImageUtils.greyImage2Array(greyImage).mkString(",")
    val in = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))
    val out = new FileOutputStream("/Users/chengli/Downloads/test_uniform/1.txt")
    copy(in, out)
  }

  test("process train file") {
    val trainDir = "/Users/chengli/Downloads/train/"
    val outputDir = "/Users/chengli/Downloads/kaggle/train/"
    val files = getListOfFiles(trainDir)

    files.foreach { file =>
      val fileName = file.getName
      val label = fileName.split("\\.")(0)
      val id = fileName.split("\\.")(1)
      val newImage = ImageUtils.resizeImage(trainDir + fileName, ImageUtils.ImageType.IMAGE_JPEG, 50, 50)
      val greyImage = ImageUtils.rgbImage2GreyImage(newImage)
      val imageContent = id + "," + label + "," + ImageUtils.greyImage2Array(greyImage).mkString(",") + "\n"
      val in = new ByteArrayInputStream(imageContent.getBytes(StandardCharsets.UTF_8))
      val out = new FileOutputStream(outputDir + fileName.replace("jpg", "txt"))
      copy(in, out)
    }
  }

  test("process test file") {
    val trainDir = "/Users/chengli/Downloads/test/"
    val outputDir = "/Users/chengli/Downloads/kaggle/test/"
    val files = getListOfFiles(trainDir)

    files.foreach { file =>
      val fileName = file.getName
      val id = fileName.split("\\.")(0)
      val newImage = ImageUtils.resizeImage(trainDir + fileName, ImageType.IMAGE_JPEG, 50, 50)
      val greyImage = ImageUtils.rgbImage2GreyImage(newImage)
      val imageContent = id + "," + ImageUtils.greyImage2Array(greyImage).mkString(",") + "\n"
      val in = new ByteArrayInputStream(imageContent.getBytes(StandardCharsets.UTF_8))
      val out = new FileOutputStream(outputDir + fileName.replace("jpg", "txt"))
      copy(in, out)
    }
  }
}
