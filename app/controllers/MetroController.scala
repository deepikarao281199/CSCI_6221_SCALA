package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import services._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MetroController @Inject()(cc: ControllerComponents, metroService: MetroService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def searchStations(query: String): Action[AnyContent] = Action.async {
    if (query.length < 3) {
      Future.successful(BadRequest("Query must have at least 3 characters"))
    } else {
      metroService.searchStations(query).map { stations =>
        Ok(Json.toJson(stations))
      }
    }
  }

  def getTrainTimings(stationCode: String): Action[AnyContent] = Action.async {
    metroService.getTrainPredictions(stationCode).map { trains =>
      Ok(Json.toJson(trains))
    }
  }
}
