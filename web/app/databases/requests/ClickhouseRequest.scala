package databases.requests

import anorm.RowParser

import scala.concurrent.Future

trait ClickhouseRequest[T] {
  val rowParser: RowParser[T]
}