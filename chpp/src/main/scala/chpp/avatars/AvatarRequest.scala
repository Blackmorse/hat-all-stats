package chpp.avatars

import chpp.AbstractRequest
import chpp.avatars.models.AvatarContainer

case class AvatarRequest(actionType: String = "players",
                         teamId: Option[Int] = None) extends AbstractRequest[AvatarContainer]("avatars", "1.1",
  "actionType" -> Some(actionType),
  "teamId" -> teamId)
