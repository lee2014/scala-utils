package db

import slick.jdbc.MySQLProfile.api._

/**
  * Created by lee on 17-5-7.
  */
case class User(id: Int, first: String, last: String) {
  def apply(id: Int, first: String, last: String): User = User(id, first, last)

  def unapply(arg: User): Option[(Int, String, String)] =
    Some((arg.id, arg.first, arg.last))
}

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def first = column[String]("first")
  def last = column[String]("last")
  def * = (id, first, last) <> (User.tupled, User.unapply)
}