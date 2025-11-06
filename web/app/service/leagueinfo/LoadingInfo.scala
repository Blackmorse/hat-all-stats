package service.leagueinfo

import java.util.Date
import play.api.libs.json.{JsString, JsValue, Json, Writes}
import zio.json.JsonEncoder

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
        "date"        -> date
      )
    }
  }

  implicit val dateEncoder: JsonEncoder[Date] = JsonEncoder[Long].contramap(_.getTime)

  implicit val encoder: JsonEncoder[LoadingInfo] = new JsonEncoder[LoadingInfo] {
    override def unsafeEncode(a: LoadingInfo, indent: Option[Int], out: zio.json.internal.Write): Unit = {
      a match {
        case Loading => out.write("""{"loadingInfo":"loading"}""")
        case Finished => out.write("""{"loadingInfo":"finished"}""")
        case Scheduled(date) =>
          out.write(s"""{"loadingInfo":"scheduled","date":${date.getTime}}""")
      }
    }
  }



}
