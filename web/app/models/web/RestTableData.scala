package models.web

import play.api.libs.json.{Json, OWrites, Writes}

case class RestTableData[T](entities: Seq[T],
                            isLastPage: Boolean)

object RestTableData {
  implicit def writes[T](implicit nested: Writes[Seq[T]]): OWrites[RestTableData[T]] =
    OWrites[RestTableData[T]](restTableData => Json.obj("isLastPage" -> restTableData.isLastPage,
      "entities" -> nested.writes(restTableData.entities)))
}