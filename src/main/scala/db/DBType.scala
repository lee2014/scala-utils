package db

import java.sql.Driver

/**
  * Created by lee on 17-3-19.
  */

object DBType extends Enumeration {
  type DBType = Value
  val MySql, PostgreSql, H2, UNKNOWN = Value

  def fromUrl(url: String): DBType = {
    url match {
      case u if u.startsWith("jdbc:mysql:") => MySql
      case u if u.startsWith("jdbc:postgresql:") => PostgreSql
      case u if u.startsWith("jdbc:h2:") => H2
      case _ => UNKNOWN
    }
  }
}

case class DBType(dBType: DBType.DBType) {
  lazy val driver: Driver = {
    val name = dBType match {
      case DBType.MySql => "com.mysql.jdbc.Driver"
      case DBType.PostgreSql => "org.postgresql.Driver"
      case DBType.H2 => "org.h2.Driver"
    }
    Class.forName(name).newInstance().asInstanceOf[Driver]
  }
}