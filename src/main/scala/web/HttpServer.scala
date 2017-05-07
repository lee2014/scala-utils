package web


import java.net.InetSocketAddress

import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.Service
import com.twitter.finagle.Http
import com.twitter.finagle.http._
import com.twitter.finagle.http.Method._
import com.twitter.finagle.http.path._
import com.twitter.util.{Await, Future}
import com.twitter.finagle.http.path.Root


/**
  * Created by lee on 17-5-7.
  */
object HttpServer extends App{

  def echoService(message: String) = new Service[Request, Response] {
    def apply(req: Request): Future[Response] = {
      val rep = Response(Version.Http11, Status.Ok)
      rep.setContentString(message)
      Future(rep)
    }
  }

  def echoJson() = new Service[Request, Response] {
    def apply(req: Request): Future[Response] = {
      val rep = Response(Version.Http11, Status.Ok)
      rep.setContentString(req.getContentString)
      Future(rep)
    }
  }

  val blackHole = new Service[Request, Response] {
    def apply(request: Request): Future[Response] = {
      Future.never
    }
  }

  val router = RoutingService.byMethodAndPathObject[Request] {
    case (Get, Root / "echo" / message) => echoService(message)
    case (Post, Root / "submit") => echoJson()
    case _ => blackHole
  }

  val server = Http.server.serve(":8080", router)
  println("The server Alive and Kicking")
  Await.ready(server)
}
