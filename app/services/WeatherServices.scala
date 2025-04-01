package services

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import models.{WeatherData, WeatherRecord, Temperature, Wind, WeatherCondition}
import repositories.WeatherRepository
import scala.concurrent.{ExecutionContext, Future}
import java.time.Instant

@Singleton
class WeatherServices @Inject()(
                                 config: Configuration,
                                 weatherRepository: WeatherRepository  // Keep the repository for historical data
                               )(implicit ec: ExecutionContext) {

  private val apiKey = config.get[String]("weather.api.key")
  private val apiUrl = config.get[String]("weather.api.url")

  // Mock implementation that returns sample data
  def getCurrentWeather(city: String): Future[Either[String, WeatherData]] = {
    // Create sample weather data for the requested city
    val weatherData = WeatherData(
      city = city,
      country = "Sample",
      temperature = Temperature(
        current = 20.5,
        min = 17.0,
        max = 23.0,
        feelsLike = 21.0
      ),
      weather = Seq(
        WeatherCondition(
          main = "Clear",
          description = "clear sky",
          icon = "01d"
        )
      ),
      wind = Wind(
        speed = 5.1,
        degrees = 230
      ),
      humidity = 65,
      pressure = 1012,
      timestamp = Instant.now().getEpochSecond
    )

    // Store mock data in repository for historical tracking
    val weatherRecord = WeatherRecord.fromWeatherData(weatherData)
    weatherRepository.create(weatherRecord).map(_ => Right(weatherData))
      .recover { case e: Exception =>
        Right(weatherData) // Still return data even if DB storage fails
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