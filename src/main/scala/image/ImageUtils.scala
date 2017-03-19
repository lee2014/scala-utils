package image

import java.awt.{Color, Dimension, Image}
import java.awt.image.{BufferedImage, PixelGrabber}
import java.io.{File, IOException}
import java.util.Locale
import javax.imageio.plugins.jpeg.JPEGImageWriteParam
import javax.imageio.{IIOImage, ImageIO, ImageWriteParam, ImageWriter}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by lee on 17-3-19.
  */
object ImageUtils {

  object ImageType extends Enumeration {
    type ImageType = Value

    val IMAGE_UNKNOWN: Int = -1
    val IMAGE_JPEG: Int = 0
    val IMAGE_PNG: Int = 1
    val IMAGE_GIF: Int = 2
  }

  /**
    * Resizes an image
    * @param imgName The image name to resize. Must be the complet path to the file
    * @param imgType int
    * @param maxWidth The image's max width
    * @param maxHeight The image's max height
    * @return A resized <code>BufferedImage</code>
    */
  def  resizeImage(imgName: String, imgType: Int, maxWidth: Int, maxHeight: Int): BufferedImage = {
    try {
      resizeImage(ImageIO.read(new File(imgName)), imgType, maxWidth, maxHeight)
    } catch  {
      case e: IOException => e.printStackTrace()
        null
    }
  }

  /**
    * Resizes an image
    * @param image The image to resize
    * @param maxWidth The image's max width
    * @param maxHeight The image's max height
    * @return A resized <code>BufferedImage</code>
    * @param imgType int
    */
  def  resizeImage(image: BufferedImage, imgType: Int, maxWidth: Int, maxHeight: Int): BufferedImage = {
    val largestDimension = new Dimension(maxWidth, maxHeight)
    // Original size
    var imageWidth: Int = image.getWidth
    var imageHeight = image.getHeight

    /*
    val aspectRatio: Float =  imageWidth.toFloat / imageHeight
    if (imageWidth > maxWidth || imageHeight > maxHeight) {

      if ( largestDimension.width.toFloat / largestDimension.height > aspectRatio) {
        largestDimension.width = Math.ceil(largestDimension.height * aspectRatio).toInt
      } else {
        largestDimension.height = Math.ceil(largestDimension.width / aspectRatio).toInt
      }

      imageWidth = largestDimension.width
      imageHeight = largestDimension.height
    }
    */
    imageWidth = largestDimension.width
    imageHeight = largestDimension.height

    createHeadlessSmoothBufferedImage(image, imgType, imageWidth, imageHeight)
  }
  /**
    * Saves an image to the disk.
    * @param image The image to save
    * @param toFileName The filename to use
    * @param imgType
    *          The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as
    *          JPEG images, or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
    * @return <code>false</code> if no appropriate writer is found
    */
  def  saveImage(image: BufferedImage,  toFileName: String, imgType: Int): Boolean = {
    try {
      ImageIO.write(image, if (imgType == ImageType.IMAGE_JPEG) "jpg" else "png", new File(toFileName))
    } catch {
      case e: IOException =>
        e.printStackTrace()
        false
    }
  }

  /**
    * Compress and save an image to the disk. Currently this method only supports
    * JPEG images.
    *
    * @param image The image to save
    * @param toFileName The filename to use
    * @param imgType
    *          The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as
    *          JPEG images, or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
    */
  def saveCompressedImage( image: BufferedImage, toFileName: String, imgType: Int): Unit = {
    try {
      if (imgType == ImageType.IMAGE_PNG) {
        throw new UnsupportedOperationException("PNG compression not implemented")
      }
      val iterator = ImageIO.getImageWritersByFormatName("jpg")
      val writer: ImageWriter = iterator.next
      val ios = ImageIO.createImageOutputStream(new File(toFileName))
      writer.setOutput(ios)
      val iwparam = new JPEGImageWriteParam(Locale.getDefault)
      iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
      iwparam.setCompressionQuality(0.7F)
      writer.write(null, new IIOImage(image, null, null), iwparam)
      ios.flush()
      writer.dispose()
      ios.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  /**
    * Creates a <code>BufferedImage</code> from an <code>Image</code>. This
    * method can function on a completely headless system. This especially
    * includes Linux and Unix systems that do not have the X11 libraries
    * installed, which are required for the AWT subsystem to operate. This method
    * uses nearest neighbor approximation, so it's quite fast. Unfortunately, the
    * result is nowhere near as nice looking as the
    * createHeadlessSmoothBufferedImage method.
    *
    * @param image The image to convert
    * @param width The desired image width
    * @param height The desired image height
    * @return The converted image
    * @param imgType int
    */
  def createHeadlessBufferedImage(image: BufferedImage,
                                  imgType: Int,
                                  width: Int,
                                  height: Int): BufferedImage = {
    var newType: Int = imgType
    if (newType == ImageType.IMAGE_PNG && hasAlpha(image)) {
      newType = BufferedImage.TYPE_INT_ARGB
    } else {
      newType = BufferedImage.TYPE_INT_RGB
    }
    val bi = new BufferedImage(width, height, newType)
    Range(0, height).foreach { y =>
      Range(0, width).foreach { x =>
        bi.setRGB(x, y, image.getRGB(x * image.getWidth() / width, y * image.getHeight() / height))
      }
    }
    bi
  }
  /**
    * Creates a <code>BufferedImage</code> from an <code>Image</code>. This
    * method can function on a completely headless system. This especially
    * includes Linux and Unix systems that do not have the X11 libraries
    * installed, which are required for the AWT subsystem to operate. The
    * resulting image will be smoothly scaled using bilinear filtering.
    *
    * @param source The image to convert
    * @param width The desired image width
    * @param height The desired image height
    * @return The converted image
    * @param imgType int
    */
  def  createHeadlessSmoothBufferedImage(source: BufferedImage,
                                         imgType: Int,
                                         width: Int,
                                         height: Int): BufferedImage = {
    var newType = imgType
    if (newType == ImageType.IMAGE_PNG && hasAlpha(source)) {
      newType = BufferedImage.TYPE_INT_ARGB
    } else {
      newType = BufferedImage.TYPE_INT_RGB
    }
    val dest = new BufferedImage(width, height, newType)
    val scalex =  width.toDouble / source.getWidth
    val scaley = height.toDouble / source.getHeight

    Range(0, height).foreach { y =>
      val sourcey = y * source.getHeight / dest.getHeight
      val ydiff = scale(y, scaley) - sourcey
      Range(0, width).foreach { x =>
        val sourcex = x * source.getWidth() / dest.getWidth()
        val xdiff = scale(x, scalex) - sourcex
        val x1 = Math.min(source.getWidth() - 1, sourcex + 1)
        val y1 = Math.min(source.getHeight() - 1, sourcey + 1)
        val rgb1 = getRGBInterpolation(source.getRGB(sourcex, sourcey), source.getRGB(x1, sourcey),
          xdiff)
        val rgb2 = getRGBInterpolation(source.getRGB(sourcex, y1), source.getRGB(x1, y1), xdiff)
        val rgb = getRGBInterpolation(rgb1, rgb2, ydiff)
        dest.setRGB(x, y, rgb)
      }
    }
    dest
  }

  private def scale(point: Int, scaled: Double): Double = point / scaled

  private def getRGBInterpolation(value1: Int, value2: Int, distance: Double): Int = {
    val alpha1 = (value1 & 0xFF000000) >>> 24
    val red1 = (value1 & 0x00FF0000) >> 16
    val green1 = (value1 & 0x0000FF00) >> 8
    val blue1 = value1 & 0x000000FF
    val alpha2 = (value2 & 0xFF000000) >>> 24
    val red2 = (value2 & 0x00FF0000) >> 16
    val green2 = (value2 & 0x0000FF00) >> 8
    val blue2 = value2 & 0x000000FF
    val rgb = ((alpha1 * (1.0d - distance) + alpha2 * distance).toInt << 24) |
      ((red1 * (1.0 - distance) + red2 * distance).toInt << 16) |
      ((green1 * (1.0 - distance) + green2 * distance).toInt << 8) |
      (blue1 * (1.0 - distance) + blue2 * distance).toInt
    rgb
  }

  /**
    * Determines if the image has transparent pixels.
    *
    * @param image The image to check for transparent pixel.s
    * @return <code>true</code> of <code>false</code>, according to the result
    */
  def hasAlpha(image: Image): Boolean = {
    try {
      val pg = new PixelGrabber(image, 0, 0, 1, 1, false)
      pg.grabPixels()
      pg.getColorModel.hasAlpha
    } catch {
      case e: InterruptedException =>
        e.printStackTrace()
        false
    }
  }

  def rgb2Grey(alpha: Int, red: Int, green: Int, blue: Int): Int = {
    var newPixel = 0
    newPixel += alpha
    newPixel = newPixel << 8
    newPixel += red
    newPixel = newPixel << 8
    newPixel += green
    newPixel = newPixel << 8
    newPixel += blue
    newPixel
  }

  def rgbImage2GreyImage(rgbImage: BufferedImage): BufferedImage = {
    val greyImage = new BufferedImage(
      rgbImage.getWidth,
      rgbImage.getHeight,
      rgbImage.getType
    )

    Range(0, greyImage.getWidth).foreach { w =>
      Range(0, greyImage.getHeight).foreach { h =>
        val color = new Color(rgbImage.getRGB(w, h))
        val r = color.getRed
        val g = color.getGreen
        val b = color.getBlue
        val grey = (0.3 * r + 0.59 * g + 0.11 * b).toInt
        val newPixel = rgb2Grey(255, grey, grey, grey)
        greyImage.setRGB(w, h, newPixel);
      }
    }

    greyImage
  }

  def greyValue(rgb: Int): Int = rgb & 0xFF

  def greyImage2Array(greyImage: BufferedImage): Array[Int] = {
    val greyArray = ArrayBuffer[Int]()

    Range(0, greyImage.getWidth).foreach { w =>
      Range(0, greyImage.getHeight).foreach { h =>
        greyArray += greyValue(greyImage.getRGB(w, h))
      }
    }
    greyArray.toArray
  }
}
