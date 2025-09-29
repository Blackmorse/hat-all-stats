package controllers

import models.web.{HattidError, RestTableData}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Action, AnyContent, BaseController, Request, Result}
import zio.{Unsafe, ZIO, ZLayer}

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

  def asyncZioPost[A, Model: Writes](zio: ZIO[Request[JsValue], HattidError, Model]): Action[JsValue] = {
    Action.async(parse.json) { request =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture {
          zio.map(model => Ok(Json.toJson(model)))
            .catchAll(error => ZIO.succeed(error.toPlayHttpResult))
            .provide(ZLayer.succeed(request))
        }
      }
    }
  }
}

abstract class RestController extends BaseController with ZioActionBuilder {
  def restTableData[T](entities: List[T], pageSize: Int): RestTableData[T] = {
    val isLastPage = entities.size <= pageSize

    val entitiesNew = if (!isLastPage) entities.dropRight(1) else entities
    RestTableData(entitiesNew, isLastPage)
  }
}
