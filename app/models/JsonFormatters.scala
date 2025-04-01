package models

import play.api.libs.json._
import java.sql.Timestamp

object JsonFormatters {
  // Format for Timestamp
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(timestamp: Timestamp): JsValue = Json.obj("time" -> timestamp.getTime)
    def reads(json: JsValue): JsResult[Timestamp] =
      (json \ "time").validate[Long].map(new Timestamp(_))
  }

  // Formats for weather domain models
  implicit val temperatureFormat: Format[Temperature] = Json.format[Temperature]
  implicit val weatherConditionFormat: Format[WeatherCondition] = Json.format[WeatherCondition]
  implicit val windFormat: Format[Wind] = Json.format[Wind]
  implicit val weatherDataFormat: Format[WeatherData] = Json.format[WeatherData]

  // Format for database model
  implicit val weatherRecordFormat: Format[WeatherRecord] = Json.format[WeatherRecord]
}