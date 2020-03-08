package controllers

import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.{AnyContent, Request}

trait MessageSupport {
  def messages(implicit request: Request[AnyContent], messagesApi: MessagesApi) = {
    val lang = if (!request.session.data.contains("lang")) "en" else request.session.apply("lang")
    messagesApi.preferred(Seq(Lang.apply(lang)))
  }
}
