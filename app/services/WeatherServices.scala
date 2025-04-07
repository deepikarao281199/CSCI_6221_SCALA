package services

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.ws._
import play.api.libs.json._
import models.{WeatherData, WeatherRecord, Temperature, Wind, WeatherCondition}
import repositories.WeatherRepository
import scala.concurrent.{ExecutionContext, Future}
import java.time.Instant

@Singleton
class WeatherServices @Inject()(
                                 ws: WSClient,
                                 config: Configuration,
                                 weatherRepository: WeatherRepository
                               )(implicit ec: ExecutionContext)  {

  private val apiKey = config.get[String]("weather.api.key")
  private val apiUrl = config.get[String]("weather.api.url")

  def getCurrentWeather(city: String): Future[Either[String, WeatherData]] = {
    val url = s"$apiUrl/weather"

    ws.url(url)
      .addQueryStringParameters(
        "q" -> city,
        "appid" -> apiKey,
        "units" -> "metric"
      )
      .get()
      .flatMap { response =>
        response.status match {
          case 200 =>
            try {
              val json = Json.parse(response.body)  // Fixed: use Json.parse
              val cityName = (json \ "name").as[String]
              val country = (json \ "sys" \ "country").as[String]

              // Parse temperature data
              val mainData = (json \ "main")
              val temperature = Temperature(
                current = (mainData \ "temp").as[Double],
                min = (mainData \ "temp_min").as[Double],
                max = (mainData \ "temp_max").as[Double],
                feelsLike = (mainData \ "feels_like").as[Double]
              )

              // Parse weather conditions
              val weatherData = (json \ "weather").as[JsArray].value.map { condition =>
                WeatherCondition(
                  main = (condition \ "main").as[String],
                  description = (condition \ "description").as[String],
                  icon = (condition \ "icon").as[String]
                )
              }.toSeq

              // Parse wind data
              val windData = (json \ "wind")
              val wind = Wind(
                speed = (windData \ "speed").as[Double],
                degrees = (windData \ "deg").as[Int]
              )

              // Parse humidity and pressure
              val humidity = (mainData \ "humidity").as[Int]
              val pressure = (mainData \ "pressure").as[Int]

              val coordinates = json \ "coord"
              val latitude = (coordinates \ "lat").as[Double].toString
              val longitude = (coordinates \ "lon").as[Double].toString

              // Create weather data object
              val weatherDataObj = WeatherData(
                city = cityName,
                country = country,
                temperature = temperature,
                weather = weatherData,
                wind = wind,
                humidity = humidity,
                pressure = pressure,
                timestamp = Instant.now().getEpochSecond,
                lat=latitude,
                lon=longitude
              )

              // Store the weather data in the database
              val weatherRecord = WeatherRecord.fromWeatherData(weatherDataObj)
              weatherRepository.create(weatherRecord).map(_ => Right(weatherDataObj))
                .recover { case ex =>
                  // If database save fails, still return the weather data
                  Right(weatherDataObj)
                }
            } catch {
              case e: Exception =>
                Future.successful(Left(s"Failed to parse weather data: ${e.getMessage}"))
            }
          case 404 =>
            Future.successful(Left(s"City '$city' not found"))
          case status =>
            Future.successful(Left(s"Weather API error: ${response.status} ${response.statusText}"))
        }
      }
      .recover {
        case e: Exception => Left(s"Failed to get weather data: ${e.getMessage}")
      }
  }
  def fetchCitySuggestions(query: String): Future[JsValue] = {
    val url = s"http://api.openweathermap.org/geo/1.0/direct?q=$query&limit=5&appid=$apiKey"

    ws.url(url).get().map { response =>
      val rawJson = response.json.as[JsArray]

      val simplifiedJson = JsArray(
        rawJson.value.map { city =>
          val name = (city \ "name").asOpt[String].getOrElse("")
          val state = (city \ "state").asOpt[String].getOrElse("")
          val country = (city \ "country").asOpt[String].getOrElse("")
          val lat = (city \ "lat").asOpt[Double].getOrElse(0.0)
          val lon = (city \ "lon").asOpt[Double].getOrElse(0.0)

          Json.obj(
            "name" -> JsString(name),
            "state" -> JsString(state),
            "country" -> JsString(country),
            "lat" -> JsNumber(lat),
            "lon" -> JsNumber(lon)
          )
        }
      )

      simplifiedJson
    }
  }

  def getHistoricalData(city: String, limit: Int = 10): Future[Seq[WeatherRecord]] = {
    weatherRepository.list(city, limit)
  }

  def getForecast(city: String): Future[Either[String, Seq[WeatherData]]] = {
    val url = s"$apiUrl/forecast"

    ws.url(url)
      .addQueryStringParameters(
        "q" -> city,
        "appid" -> apiKey,
        "units" -> "metric",
        "cnt" -> "40"  // 5 days forecast, 3-hour step (8 per day)
      )
      .get()
      .map { response =>
        response.status match {
          case 200 =>
            try {
              val json = Json.parse(response.body)  // Fixed: use Json.parse
              val cityData = (json \ "city")
              val cityName = (cityData \ "name").as[String]
              val country = (cityData \ "country").as[String]

              val forecastList = (json \ "list").as[JsArray].value.map { item =>
                val dt = (item \ "dt").as[Long]
                val mainData = (item \ "main")
                val weatherData = (item \ "weather").as[JsArray].value.map { condition =>
                  WeatherCondition(
                    main = (condition \ "main").as[String],
                    description = (condition \ "description").as[String],
                    icon = (condition \ "icon").as[String]
                  )
                }.toSeq

                val windData = (item \ "wind")
                val coord = (cityData \ "coord")
                val lat = (coord \ "lat").as[Double].toString
                val lon = (coord \ "lon").as[Double].toString

                WeatherData(
                  city = cityName,
                  country = country,
                  temperature = Temperature(
                    current = (mainData \ "temp").as[Double],
                    min = (mainData \ "temp_min").as[Double],
                    max = (mainData \ "temp_max").as[Double],
                    feelsLike = (mainData \ "feels_like").as[Double]
                  ),
                  weather = weatherData,
                  wind = Wind(
                    speed = (windData \ "speed").as[Double],
                    degrees = (windData \ "deg").as[Int]
                  ),
                  humidity = (mainData \ "humidity").as[Int],
                  pressure = (mainData \ "pressure").as[Int],
                  timestamp = dt,
                  lat=lat,
                  lon=lon,
                )
              }.toSeq

              // Group forecast data by day to get daily forecast
              val dailyForecast = forecastList
                .groupBy(data => {
                  val instant = Instant.ofEpochSecond(data.timestamp)
                  instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate
                })
                .map { case (_, forecasts) =>
                  // Use the middle of the day forecast as representative
                  val midDayForecast = forecasts.sortBy(_.timestamp).drop(forecasts.size / 2).head
                  midDayForecast
                }
                .toSeq
                .sortBy(_.timestamp)
                .take(10)  // Limit to 10 days

              Right(dailyForecast)
            } catch {
              case e: Exception =>
                Left(s"Failed to parse forecast data: ${e.getMessage}")
            }
          case 404 =>
            Left(s"City '$city' not found")
          case status =>
            Left(s"Weather API error: ${response.status} ${response.statusText}")
        }
      }
      .recover {
        case e: Exception => Left(s"Failed to get forecast data: ${e.getMessage}")
      }
  }
}