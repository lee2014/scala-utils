package db

import java.sql.Driver

/**
  * Created by lee on 17-3-19.
  */
case class DatabaseAdapter()
case class MySQLAdapter() extends DatabaseAdapter
case class PostgreSqlAdapter() extends DatabaseAdapter
case class H2Adapter() extends DatabaseAdapter


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

case class DBType(typ: DBType) {
  lazy val driver: Driver = {
    val name = typ match {
      case DBType.MySql => "com.mysql.jdbc.Driver"
      case DBType.PostgreSql => "org.postgresql.Driver"
      case DBType.H2 => "org.h2.Driver"
    }
    Class.forName(name).newInstance().asInstanceOf[Driver]
  }
  lazy val adapter: DatabaseAdapter = {
    typ match {
      case DBType.MySql => new MySQLAdapter
      case DBType.PostgreSql => new PostgreSqlAdapter
      case DBType.H2 => new H2Adapter
    }
  }
}