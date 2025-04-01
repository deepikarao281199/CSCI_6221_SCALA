package models

import java.sql.Timestamp
import play.api.libs.json._

case class WeatherRecord(
                          id: Option[Long] = None,
                          city: String,
                          country: String,
                          temperature: Double,
                          minTemp: Double,
                          maxTemp: Double,
                          feelsLike: Double,
                          humidity: Int,
                          pressure: Int,
                          main: String,
                          description: String,
                          icon: String,
                          windSpeed: Double,
                          windDeg: Int,
                          timestamp: Long,
                          createdAt: Option[Timestamp] = None
                        )

object WeatherRecord {
  import models.JsonFormatters._

  def fromWeatherData(data: WeatherData): WeatherRecord = {
    WeatherRecord(
      id = None,
      city = data.city,
      country = data.country,
      temperature = data.temperature.current,
      minTemp = data.temperature.min,
      maxTemp = data.temperature.max,
      feelsLike = data.temperature.feelsLike,
      humidity = data.humidity,
      pressure = data.pressure,
      main = data.weather.headOption.map(_.main).getOrElse(""),
      description = data.weather.headOption.map(_.description).getOrElse(""),
      icon = data.weather.headOption.map(_.icon).getOrElse(""),
      windSpeed = data.wind.speed,
      windDeg = data.wind.degrees,
      timestamp = data.timestamp,
      createdAt = None
    )
  }

  implicit val weatherRecordFormat: Format[WeatherRecord] = Json.format[WeatherRecord]
}