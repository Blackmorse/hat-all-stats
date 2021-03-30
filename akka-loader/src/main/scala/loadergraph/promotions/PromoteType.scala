package loadergraph.promotions

trait PromoteType {
  def value: Int
  def valueStr: String
}

case object Auto extends PromoteType {
  val value = 0
  val valueStr = "auto"
}

case object Qualify extends PromoteType {
  val value = 1
  val valueStr = "qualify"
}




