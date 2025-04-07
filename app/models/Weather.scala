package models

// Models for weather data from API
case class WeatherData(
                        city: String,
                        country: String,
                        temperature: Temperature,
                        weather: Seq[WeatherCondition],
                        wind: Wind,
                        humidity: Int,
                        pressure: Int,
                        timestamp: Long,
                        lat:String,
                        lon:String,


                      )
case class Temperature(
                        current: Double,
                        min: Double,
                        max: Double,
                        feelsLike: Double
                      )

case class WeatherCondition(
                             main: String,
                             description: String,
                             icon: String
                           )

case class Wind(
                 speed: Double,
                 degrees: Int
               )
