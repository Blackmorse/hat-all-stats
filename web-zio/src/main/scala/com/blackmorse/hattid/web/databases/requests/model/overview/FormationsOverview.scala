package com.blackmorse.hattid.web.databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class FormationsOverview(formation: String, count: Int)

object FormationsOverview {
  implicit val jsonEncoder: JsonEncoder[FormationsOverview] = DeriveJsonEncoder.gen[FormationsOverview]

  val mapper: RowParser[FormationsOverview] = {
    get[String]("formation") ~
    get[Int]("count") map {
      case formation ~ count => FormationsOverview(formation, count)
    }
  }
}
