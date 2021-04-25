package promotions

import chpp.worlddetails.models.League
import loadergraph.promotions.{Auto, DivisionDownStrategy, DivisionTeams, None, PromotionListsMerger, Qualify, Reverse, Straightforward}
import models.clickhouse.PromotionModelCH
import models.stream.StreamTeam
import promotions.PromotionsCalculator.{downStrategies, reverseOrdering, straightOrdering}

object PromotionsCalculator {
  val downStrategies = Map(
    1 -> DivisionDownStrategy(Reverse, Straightforward),
    2 -> DivisionDownStrategy(Reverse, Straightforward),
    3 -> DivisionDownStrategy(Reverse, Straightforward),
    4 -> DivisionDownStrategy(Reverse, Straightforward),
    5 -> DivisionDownStrategy(Reverse, Straightforward),
    6 -> DivisionDownStrategy(Reverse, Reverse),
    7 -> DivisionDownStrategy(Reverse, Reverse),
    8 -> DivisionDownStrategy(Reverse, Reverse),
    9 -> DivisionDownStrategy(Reverse, Reverse),
    10 -> DivisionDownStrategy(None, None),
  )

  private val straightOrdering: Ordering[StreamTeam] = Ordering
    .by[StreamTeam, Int](_.position)
    .orElseBy(- _.points)
    .orElseBy(- _.diff)
    .orElseBy(- _.scored)

  private val reverseOrdering = straightOrdering.reverse

  def calculatePromotions(promotionsCalculator: PromotionsCalculator, levels: Int): List[PromotionModelCH] = {
    if(promotionsCalculator.divisionTeams.isEmpty) return List()
    (1 until levels).flatMap(level => promotionsCalculator.calculatePromotionsForDivision(level)).toList
  }
}

class PromotionsCalculator(league: League, teams: List[StreamTeam]) {
  private val divisionTeams: Map[Int, DivisionTeams] = teams.groupBy(_.leagueUnit.level)
    .map{case (level, teams) => (level, DivisionTeams(level, if (level != league.numberOfLevels ) downStrategies(level) else DivisionDownStrategy(None, None), teams))}


  def calculatePromotionsForDivision(divisionLevel: Int): List[PromotionModelCH] = {
    val downStrategy = divisionTeams(divisionLevel).divisionDownStrategy

    val qualifyGoingDownTeams = divisionTeams(divisionLevel).teams
      .filter(team => team.position == 5 || team.position == 6)
      .sorted(PromotionsCalculator.straightOrdering)

    val autoGoingDownTeams = divisionTeams(divisionLevel).teams
      .filter(team => team.position == 7 || team.position == 8)
      .sorted(PromotionsCalculator.straightOrdering)

    val goingUpTeams = divisionTeams(divisionLevel + 1).teams
      .sorted(PromotionsCalculator.straightOrdering)
      .take(qualifyGoingDownTeams.size + autoGoingDownTeams.size)

    val autoGoingUpTeams = goingUpTeams
      .take(autoGoingDownTeams.size)
      .sorted(if (downStrategy.autoPromoteStrategy == Straightforward) straightOrdering else reverseOrdering)

    val qualifyGoingUpTeams = goingUpTeams
      .slice(autoGoingDownTeams.size, autoGoingDownTeams.size + qualifyGoingDownTeams.size)
      .sorted(if (downStrategy.qualifyPromoteStrategy == Straightforward) straightOrdering else reverseOrdering)

    val autoPromotions = PromotionListsMerger.merge(leagueId = league.leagueId,
      season = league.season - league.seasonOffset,
      upDivisionLevel = divisionLevel,
      promoteType = Auto,
      leftList = autoGoingDownTeams,
      rightList = autoGoingUpTeams
    )

    val qualifyPromotions = PromotionListsMerger.merge(leagueId = league.leagueId,
      season = league.season - league.seasonOffset,
      upDivisionLevel = divisionLevel,
      promoteType = Qualify,
      leftList = qualifyGoingDownTeams,
      rightList = qualifyGoingUpTeams
    )

    autoPromotions ++ qualifyPromotions
  }
}
