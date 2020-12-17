package databases.requests

import anorm.RowParser

import scala.concurrent.Future

trait ClickhouseRequest[T] {
  val rowParser: RowParser[T]
}

object ClickhouseRequest {
  def roleIdCase(fieldName: String) =
    s"""
       |caseWithExpression($fieldName,
       |       100, 'keeper',
       |       101, 'wingback',
       |       102, 'defender',
       |       103, 'defender',
       |       104, 'defender',
       |       105, 'wingback',
       |       106, 'winger',
       |       107, 'midfielder',
       |       108, 'midfielder',
       |       109, 'midfielder',
       |       110, 'winger',
       |       111, 'forward',
       |       112, 'forward',
       |       113, 'forward',
       |     '')""".stripMargin
}