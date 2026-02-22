package com.blackmorse.hattid.web.service.leagueinfo

import zio.json.JsonEncoder

import java.util.Date

trait LoadingInfo

object Loading extends LoadingInfo

object Finished extends LoadingInfo

case class Scheduled(date: Date) extends LoadingInfo

object LoadingInfo {

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
