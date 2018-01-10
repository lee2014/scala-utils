package data

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

/**
  * Created by lee on 17-3-20.
  */
object Serialization {
  val stream = new ByteArrayOutputStream()
  val oos = new ObjectOutputStream(stream)

  def serialize(value: Any): Array[Byte] = {
    oos.writeObject(value)
    val bytes = stream.toByteArray
    oos.reset()
    stream.reset()
    bytes
  }

  def deserialize(bytes: Array[Byte]): Any = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject
    ois.close()
    value
  }
}
