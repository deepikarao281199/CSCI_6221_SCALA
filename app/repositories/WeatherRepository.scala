
package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.WeatherRecord
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

@Singleton
class WeatherRepository @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider
                                 )(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class WeatherRecordsTable(tag: Tag) extends Table[WeatherRecord](tag, "weather_records") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def city = column[String]("city")
    def country = column[String]("country")
    def temperature = column[Double]("temperature")
    def minTemp = column[Double]("min_temp")
    def maxTemp = column[Double]("max_temp")
    def feelsLike = column[Double]("feels_like")
    def humidity = column[Int]("humidity")
    def pressure = column[Int]("pressure")
    def main = column[String]("main")
    def description = column[String]("description")
    def icon = column[String]("icon")
    def windSpeed = column[Double]("wind_speed")
    def windDeg = column[Int]("wind_deg")
    def timestamp = column[Long]("timestamp")
    def createdAt = column[Timestamp]("created_at")

    def * = (
      id.?, city, country, temperature, minTemp, maxTemp, feelsLike,
      humidity, pressure, main, description, icon, windSpeed, windDeg,
      timestamp, createdAt.?
    ) <> ((WeatherRecord.apply _).tupled, WeatherRecord.unapply)
  }

  val weatherRecords = TableQuery[WeatherRecordsTable]

  def create(weatherRecord: WeatherRecord): Future[WeatherRecord] = {
    val query = (weatherRecords returning weatherRecords.map(_.id)
      into ((record, id) => record.copy(id = Some(id)))
      ) += weatherRecord

    db.run(query)
  }

  def list(city: String, limit: Int = 10): Future[Seq[WeatherRecord]] = {
    val query = weatherRecords
      .filter(_.city === city)
      .sortBy(_.timestamp.desc)
      .take(limit)

    db.run(query.result)
  }

  def getLatest(city: String): Future[Option[WeatherRecord]] = {
    val query = weatherRecords
      .filter(_.city === city)
      .sortBy(_.timestamp.desc)
      .take(1)

    db.run(query.result.headOption)
  }
}