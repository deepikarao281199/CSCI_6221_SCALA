package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.WeatherServices
import scala.concurrent.{ExecutionContext, Future}
import models.JsonFormatters._

@Singleton
class WeatherController @Inject()(
                                   val controllerComponents: ControllerComponents,
                                   weatherService: WeatherServices
                                 )(implicit ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def dashboard(city: String = "London"): Action[AnyContent] = Action.async { implicit request =>
    val cityName = if (city.isEmpty) "London" else city

    weatherService.getCurrentWeather(cityName).flatMap {
      case Right(weatherData) =>
        // Get both historical data and forecast
        val historicalDataFuture = weatherService.getHistoricalData(cityName)
        val forecastFuture = weatherService.getForecast(cityName)
        val clothingSuggestion = getClothingSuggestion(weatherData.temperature.current)  // Get clothing suggestion

        for {
          historicalData <- historicalDataFuture
          forecastResult <- forecastFuture
        } yield {
          val forecast = forecastResult.getOrElse(Seq.empty)
          // Pass the clothing suggestion as part of the parameters
          Ok(views.html.dashboard(weatherData, historicalData, forecast, clothingSuggestion))  // Pass clothing suggestion here
        }
      case Left(error) =>
        Future.successful(NotFound(views.html.error("Weather Error", error)))
    }
  }

  def getWeather(city: String): Action[AnyContent] = Action.async {
    weatherService.getCurrentWeather(city).map {
      case Right(weatherData) => Ok(Json.toJson(weatherData))
      case Left(error) => NotFound(Json.obj("error" -> error))
    }
  }

  def getHistoricalData(city: String, limit: Int = 10): Action[AnyContent] = Action.async {
    weatherService.getHistoricalData(city, limit).map { data =>
      Ok(Json.toJson(data))
    }
  }
  def getCitySuggestions(q: String): Action[AnyContent] = Action.async {
    weatherService.fetchCitySuggestions(q).map { suggestions =>
      Ok(suggestions)
    }
  }

  def getForecast(city: String): Action[AnyContent] = Action.async {
    weatherService.getForecast(city).map {
      case Right(forecastData) => Ok(Json.toJson(forecastData))
      case Left(error) => NotFound(Json.obj("error" -> error))
    }
  }

  // Method to get clothing suggestion based on the temperature
  def getClothingSuggestion(temperature: Double): String = {
    if (temperature < 5) {
      "Sweater, coat, gloves, scarf"
    } else if (temperature >= 5 && temperature <= 20) {
      "Light jacket, full sleeves"
    } else if (temperature > 20) {
      "Light colors, breathable fabric, sunglasses, cap"
    } else {
      "No suggestion available"
    }
  }
}
