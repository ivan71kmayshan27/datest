package com.example

//#quick-start-server
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.mongodb.scala._
import org.mongodb.scala.model.{FindOneAndUpdateOptions, ReturnDocument, Updates}
import configs.{MongoSettings, ServiceContext}
import cats.data.Validated._
import cats.syntax.validated._

import scala.concurrent.duration._
import cats.syntax.either._
import com.example.Commons.ErrorsOr

//#main-class
object QuickstartServer extends App {

  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.http.scaladsl.server.Directives._
  import akka.stream.ActorMaterializer
  import scala.io.StdIn

  //Thread.sleep(5000)



  def initContexts(): ErrorsOr[ServiceContext] = {
    import cats.implicits._

    ( MongoSettings.uri,
      MongoSettings.dbName).mapN(ServiceContext).toEither
  }

  initContexts().leftMap { errors =>
    println(s"Cannot start server: [${errors mkString ", "}]")
    System.exit(1)
  } foreach{ sc =>
    runService(sc)
  }

  def runService(s: ServiceContext): Unit = {
    implicit val db: MongoDatabase = MongoClient(s.mongoDBUri).getDatabase(s.mongoDBName)
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // needed for the future flatMap/onComplete in the end

    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~ path("users") {
        get {
          val users = Await.result(
            db.getCollection("users")
              .findOneAndUpdate(
                Document(),
                Updates.inc("visits", 1),
                FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER)).toFuture, 10 seconds)
          users.toMap.get("visits") match {
            case Some(value) if value.isInt32 => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1> total visits: ${value.asInt32} <h1>"))
            case _ => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1> unexpected error <h1>"))
          }
        }
      }
    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  }

}

