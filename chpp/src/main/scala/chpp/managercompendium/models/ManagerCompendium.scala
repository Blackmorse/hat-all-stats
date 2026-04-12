package chpp.managercompendium.models

import com.lucidchart.open.xtract.{XmlReader, __}

case class ManagerCompendium(manager: Manager)

object ManagerCompendium {
  implicit val reader: XmlReader[ManagerCompendium] = (
    (__ \ "Manager").read[Manager]
  ).map(apply)
}