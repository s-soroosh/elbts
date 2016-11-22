package de.zalando.elbts

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */

trait RestAPI {
  def healthcheck(): Future[String] = Future.successful("ELBTS is alive")
}

class RestRoutes extends RestAPI {

  import StatusCodes._


  val healthcheckRoute = pathPrefix("healthcheck") {
    pathEndOrSingleSlash {
      get {
        onSuccess(healthcheck()) { msg =>
          complete(OK, msg)
        }
      }
    }
  }
}