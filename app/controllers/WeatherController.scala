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
        weatherService.getHistoricalData(cityName).map { historicalData =>
          Ok(views.html.dashboard(weatherData, historicalData))
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
}