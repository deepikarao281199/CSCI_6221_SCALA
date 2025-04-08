package services

import javax.inject._
import models._
import play.api.libs.json._


import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MetroService @Inject()(ws: play.api.libs.ws.WSClient)(implicit ec: ExecutionContext) {

  val apiKey = "85786a0d82a44460b060e22f10dd890f";

  // Method to get all stations
  def getStations: Future[Seq[Station]] = {
    val url = s"https://api.wmata.com/Rail.svc/json/jStations?api_key=$apiKey"
    ws.url(url).get().map { response =>
      (response.json \ "Stations").as[Seq[Station]]
    }
  }

  // Method to get train predictions based on station name
  def getTrainPredictions(stationCode: String): Future[Seq[TrainPrediction]] = {
    val url = s"https://api.wmata.com/StationPrediction.svc/json/GetPrediction/$stationCode?api_key=$apiKey"
    ws.url(url).get().map { response =>
      (response.json \ "Trains").as[Seq[TrainPrediction]]
    }
  }

  // Method to get station suggestions based on partial station name
  def searchStations(query: String): Future[Seq[Station]] = {
    getStations.map { stations =>
      stations.filter(_.Name.toLowerCase.contains(query.toLowerCase))
    }
  }
}
