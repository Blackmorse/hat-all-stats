package controllers

import models.web.RestTableData
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{BaseController, Result}

abstract class RestController extends BaseController {
  def restTableDataJson[T](entities: List[T], pageSize: Int)(implicit writes: Writes[T]): Result = {
      val isLastPage = entities.size <= pageSize

      val entitiesNew = if(!isLastPage) entities.dropRight(1) else entities
      val restTableData = RestTableData(entitiesNew, isLastPage)
      Ok(Json.toJson(restTableData))
  }
}
