package loadergraph.promotions

trait DownStrategy

object Straightforward extends DownStrategy
object Reverse extends DownStrategy
object None extends DownStrategy

case class DivisionDownStrategy(qualifyPromoteStrategy: DownStrategy, autoPromoteStrategy: DownStrategy)