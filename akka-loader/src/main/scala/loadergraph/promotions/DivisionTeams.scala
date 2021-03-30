package loadergraph.promotions

import models.stream.StreamTeam

case class DivisionTeams(divisionLevel: Int, divisionDownStrategy: DivisionDownStrategy, teams: List[StreamTeam])
