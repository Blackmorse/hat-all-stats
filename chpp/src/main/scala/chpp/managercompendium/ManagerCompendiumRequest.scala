package chpp.managercompendium

import chpp.AbstractRequest
import chpp.managercompendium.models.ManagerCompendium

case class ManagerCompendiumRequest(userId: Option[Long] = None) extends AbstractRequest[ManagerCompendium]("managercompendium", "1.7",
"userId" -> userId)
