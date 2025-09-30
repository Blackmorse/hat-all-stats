package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class LoggingHattid[A](action: Action[A]) extends Action[A]  {

  def apply(request: Request[A]): Future[Result] = {

    if (new Random().nextDouble() > 0.5) {
      action(request)
    } else {
      Future(Forbidden("Access denied"))(using executionContext)
    }
  }

  override def parser           = action.parser
  override def executionContext = action.executionContext
}

class LoggingAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    if (new Random().nextDouble() > 0.5) {
      block(request)
    } else {
      Future(Forbidden("Access denied"))
    }
  }
  override def composeAction[A](action: Action[A]) = LoggingHattid(action)
}
