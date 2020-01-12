package hattrick

import com.blackmorse.hattrick.HattrickApi
import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class Hattrick @Inject() (val configuration: Configuration) {
  val api = new HattrickApi(configuration.get[String]("hattrick.customerKey"),
    configuration.get[String]("hattrick.customerSecret"),
    configuration.get[String]("hattrick.accessToken"),
    configuration.get[String]("hattrick.accessTokenSecret"))
}
