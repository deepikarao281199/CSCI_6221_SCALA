package models

import play.api.libs.json._

case class Station(
                    Code: String,
                    Name: String
                  )

case class TrainPrediction(
                            Car: Option[String],
                            Destination: String,
                            DestinationCode: Option[String],
                            DestinationName: Option[String],
                            Group: Option[String],
                            Line: Option[String],
                            LocationCode: String,
                            LocationName: String,
                            Min: String
                          )

case class TrainPredictionsResponse(
                                     Trains: Seq[TrainPrediction]
                                   )

case class StationListResponse(
                                Stations: Seq[Station]
                              )

// Add implicit Reads and Writes for Station
object Station {
  // Implicit Reads for Station to deserialize it from JSON
  implicit val stationReads: Reads[Station] = Json.reads[Station]

  // Implicit Writes for Station to serialize it to JSON
  implicit val stationWrites: Writes[Station] = Json.writes[Station]
}

object TrainPrediction {
  // Implicit Reads for TrainPrediction to deserialize it from JSON
  implicit val trainPredictionReads: Reads[TrainPrediction] = Json.reads[TrainPrediction]

  // Implicit Writes for TrainPrediction to serialize it to JSON
  implicit val trainPredictionWrites: Writes[TrainPrediction] = Json.writes[TrainPrediction]
}

object TrainPredictionsResponse {
  // Implicit Writes for TrainPredictionsResponse to serialize it to JSON
  implicit val trainPredictionsResponseWrites: Writes[TrainPredictionsResponse] = Json.writes[TrainPredictionsResponse]
}

object StationListResponse {
  // Implicit Reads for StationListResponse to deserialize it from JSON
  implicit val stationListResponseReads: Reads[StationListResponse] = Json.reads[StationListResponse]
}
