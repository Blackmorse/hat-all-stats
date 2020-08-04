package controllers

import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.{AnyContent, Request}

trait MessageSupport {
  def messages(implicit request: Request[AnyContent], messagesApi: MessagesApi) = {
    val lang = request.cookies.get("lang").map(_.value).getOrElse("en")
    messagesApi.preferred(Seq(Lang.apply(lang)))
  }
}
