package models.clickhouse.overview

import anorm.SqlParser.get
import anorm.~

final case class CountsModel(count: Int)

object CountsModel {
  val mapper = {
      get[Int]("count") map (CountsModel(_))
  }
}