package databases.requests

import anorm.RowParser
import databases.requests.model.Roles


trait ClickhouseRequest[T] {
  val rowParser: RowParser[T]
}

object ClickhouseRequest {
  def roleIdCase(fieldName: String): String = {
    val rolesList = (for(role <- Roles.all;
        id <- role.htIds) yield s"$id, ${role.internalId},")
      .mkString("\n")

    s"""
       |caseWithExpression($fieldName,
       |$rolesList
       |0)""".stripMargin
  }
}