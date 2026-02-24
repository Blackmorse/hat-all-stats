package com.blackmorse.hattid.web.databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}

case class FormationsModel(formation: String, count: Int)

object FormationsModel {
  val mapper: RowParser[FormationsModel] = {
    get[String]("formation") ~
    get[Int]("count") map {
      case formation ~ count => FormationsModel(formation, count)
    }
  }
}
