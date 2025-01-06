package service.leagueinfo

import java.util.Date

import play.api.libs.json.{JsString, JsValue, Json, Writes}

trait LoadingInfo

object Loading extends LoadingInfo

object Finished extends LoadingInfo

case class Scheduled(date: Date) extends LoadingInfo

object LoadingInfo {
  implicit val writes: Writes[LoadingInfo] = new Writes[LoadingInfo] {
    override def writes(o: LoadingInfo): JsValue = o match {
      case Loading => Json.obj("loadingInfo" -> JsString("loading"))
      case Finished => Json.obj("loadingInfo" -> JsString("finished"))
      case Scheduled(date) => Json.obj(
        "loadingInfo" -> JsString("scheduled"),
        "date" -> date
      )
    }
  }

}
