package controllers

import models.web.RestTableData
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{BaseController, Result}

abstract class RestController extends BaseController {
  def restTableDataJson[T](entities: List[T], pageSize: Int)(implicit writes: Writes[T]): Result = {
      val rtd = restTableData[T](entities, pageSize)
      Ok(Json.toJson(rtd))
  }

  def restTableData[T](entities: List[T], pageSize: Int): RestTableData[T] = {
    val isLastPage = entities.size <= pageSize

    val entitiesNew = if (!isLastPage) entities.dropRight(1) else entities
    RestTableData(entitiesNew, isLastPage)
  }
}
