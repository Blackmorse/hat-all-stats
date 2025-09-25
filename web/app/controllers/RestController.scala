package controllers

import models.web.{HattidError, RestTableData}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AnyContent, BaseController, Result}
import zio.{Unsafe, ZIO}

trait ZioActionBuilder {
  self: BaseController =>

  private val runtime = zio.Runtime.default

  def asyncZio[A, Model : Writes](zio: ZIO[Any, HattidError, Model]): play.api.mvc.Action[AnyContent] = {
    Action.async { _ =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture(
          zio.map(model => Ok(Json.toJson(model)))
            .catchAll(error => ZIO.succeed(error.toPlayHttpResult))
        )
      }
    }
  }
}

abstract class RestController extends BaseController with ZioActionBuilder {
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
