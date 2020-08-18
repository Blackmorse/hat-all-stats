package models.web

import play.api.libs.json.{Json, OWrites, Writes}

case class RestTableData[T](entities: List[T],
                            isLastPage: Boolean)

object RestTableData {
  implicit def writes[T](implicit nested: Writes[List[T]]): Writes[RestTableData[T]] =
    OWrites[RestTableData[T]](restTableData => Json.obj("isLastPage" -> restTableData.isLastPage,
      "entities" -> nested.writes(restTableData.entities)))
}