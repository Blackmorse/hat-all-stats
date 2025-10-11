package controllers

import cache.ZioCacheModule.HattidEnv
import models.web.{HattidError, RestTableData}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Action, AnyContent, BaseController, Request, Result}
import zio.{Unsafe, ZIO, ZLayer}

trait ZioActionBuilder {
  self: RestController =>

  private val runtime = zio.Runtime.default

  def asyncZio[A, Model : Writes](zio: ZIO[HattidEnv, HattidError, Model]): play.api.mvc.Action[AnyContent] = {
    Action.async { _ =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture(
          zio.map(model => Ok(Json.toJson(model)))
            .catchAll(error => ZIO.succeed(error.toPlayHttpResult))
            .provideEnvironment(self.env)
        )
      }
    }
  }

  def asyncZioPost[A, Model: Writes](zio: ZIO[Request[JsValue] & HattidEnv, HattidError, Model]): Action[JsValue] = {
    Action.async(parse.json) { request =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture {
          zio.map(model => Ok(Json.toJson(model)))
            .catchAll(error => ZIO.succeed(error.toPlayHttpResult))
            .provideSomeEnvironment[Request[JsValue]](env => self.env ++ env)
            .provide(ZLayer.succeed(request))
        }
      }
    }
  }
}

abstract class RestController(val env: zio.ZEnvironment[HattidEnv]) extends BaseController with ZioActionBuilder {
  def restTableData[T](entities: List[T], pageSize: Int): RestTableData[T] = {
    val isLastPage = entities.size <= pageSize

    val entitiesNew = if (!isLastPage) entities.dropRight(1) else entities
    RestTableData(entitiesNew, isLastPage)
  }
}
