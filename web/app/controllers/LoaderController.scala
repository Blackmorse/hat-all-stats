package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

@Singleton
class LoaderController @Inject()(val controllerComponents: ControllerComponents
                                ) extends BaseController{
  def leagueRound(season: Int) = Action {
    println(season)
    Ok("asd")
  }
}
