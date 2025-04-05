package services

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.ws._
import play.api.libs.json._
import models.JsonFormatters._
import models.{WeatherData, WeatherRecord, Temperature, Wind, WeatherCondition}
import repositories.WeatherRepository
import scala.concurrent.{ExecutionContext, Future}
import java.time.Instant

@Singleton
class WeatherServices @Inject()(ws: WSClient)(
                                 config: Configuration,
                                 weatherRepository: WeatherRepository  // Keep the repository for historical data
                               )(implicit ec: ExecutionContext) {


    private val apiKey = config.get[String]("weather.api.key")
  private val apiUrl = config.get[String]("weather.api.url")

    def getCurrentWeather(city: String): Future[Either[String, WeatherData]] = {
      val request = ws.url(apiUrl)
        .withQueryStringParameters(
          "q" -> city,
          "appid" -> apiKey,
          "units" -> "metric"
        )

      request.get().flatMap { response =>
        if (response.status == 200) {
          val json = response.json
          // Extract data from JSON and convert to your model
          val weatherData = WeatherData(
            city = (json \ "name").as[String],
            country = (json \ "sys" \ "country").as[String],
            temperature = Temperature(
              current = (json \ "main" \ "temp").as[Double],
              min = (json \ "main" \ "temp_min").as[Double],
              max = (json \ "main" \ "temp_max").as[Double],
              feelsLike = (json \ "main" \ "feels_like").as[Double]
            ),
            weather = (json \ "weather").as[Seq[WeatherCondition]],
            wind = Wind(
              speed = (json \ "wind" \ "speed").as[Double],
              degrees = (json \ "wind" \ "deg").as[Int]
            ),
            humidity = (json \ "main" \ "humidity").as[Int],
            pressure = (json \ "main" \ "pressure").as[Int],
            timestamp = (json \ "dt").as[Long]
          )

          // Optionally store to DB
          val weatherRecord = WeatherRecord.fromWeatherData(weatherData)
          weatherRepository.create(weatherRecord).map(_ => Right(weatherData))

        } else {
          Future.successful(Left(s"Failed to fetch weather: ${response.statusText}"))
        }
      } recover {
        case ex: Exception => Left(s"API call error: ${ex.getMessage}")
      }
    }
  def getHistoricalData(city: String, limit: Int = 10): Future[Seq[WeatherRecord]] = {
    // Try to get real historical data from the repository
    weatherRepository.list(city, limit)
      .recover { case e: Exception =>
        // If repository access fails, return empty list
        Seq.empty
      }
  }
}