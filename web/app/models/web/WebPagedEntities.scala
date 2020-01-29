package models.web

import service.DefaultService

case class WebPagedEntities[T](entities: List[T], page: Int, prevUrl: Option[String], nextUrl: Option[String])

object WebPagedEntities {
  def apply[T](entities: List[T],
               page: Int,
               pageUrlFunc: Int => String): WebPagedEntities[T] = {
    val prevUrl = if(page <= 0) None else Some(pageUrlFunc(page - 1))
    val nextUrl = if (entities.size < DefaultService.PAGE_SIZE) None else Some(pageUrlFunc(page + 1))
    WebPagedEntities(entities, page, prevUrl, nextUrl)
  }
}




