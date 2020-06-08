package models.web

case class WebPagedEntities[T](entities: List[T], pageInfo: PageInfo, selectedId: Option[Long])

case class PageInfo(page: Int, pageSize: Int, prevUrl: Option[String], nextUrl: Option[String])

object WebPagedEntities {
  def apply[T](entities: List[T],
               page: Int,
               pageSize: Int,
               pageUrlFunc: Int => String,
               selectedId: Option[Long]): WebPagedEntities[T] = {
    val prevUrl = if(page <= 0) None else Some(pageUrlFunc(page - 1))
    val nextUrl = if (entities.size < pageSize + 1) None else Some(pageUrlFunc(page + 1))
    WebPagedEntities(if (entities.size == pageSize + 1) entities.dropRight(1) else entities,
      PageInfo(page, pageSize, prevUrl, nextUrl),
      selectedId)
  }
}




