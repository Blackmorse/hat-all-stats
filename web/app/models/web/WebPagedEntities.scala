package models.web

import service.DefaultService

case class WebPagedEntities[T](entities: List[T], pageInfo: PageInfo)

case class PageInfo(page: Int, prevUrl: Option[String], nextUrl: Option[String])

object WebPagedEntities {
  def apply[T](entities: List[T],
               page: Int,
               pageUrlFunc: Int => String): WebPagedEntities[T] = {
    val prevUrl = if(page <= 0) None else Some(pageUrlFunc(page - 1))
    val nextUrl = if (entities.size < DefaultService.PAGE_SIZE + 1) None else Some(pageUrlFunc(page + 1))
    WebPagedEntities(if (entities.size == DefaultService.PAGE_SIZE + 1) entities.dropRight(1) else entities, PageInfo(page, prevUrl, nextUrl))
  }
}




