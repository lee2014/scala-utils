package util

import java.io.File


/**
  * Created by lee on 17-3-19.
  */
object Util {

  def require(requirement: Boolean, message: String): Unit = {
    if (!requirement) throw new IllegalArgumentException(message)
  }

  def getListOfFiles(dir: String): Seq[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toSeq ++
        d.listFiles.filter(_.isDirectory).flatMap(_.listFiles).toSeq
    } else {
      Seq[File]()
    }
  }
}
