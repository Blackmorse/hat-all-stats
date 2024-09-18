package service.leagueunit

import chpp.leaguefixtures.models.{LeagueFixtures, Match}
import chpp.matchesarchive.models.{AwayTeam, HomeTeam}
import models.web.{Asc, Desc, SortingDirection}

import javax.inject.Singleton
import scala.Ordering.Implicits._
import scala.collection.mutable

@Singleton
class LeagueUnitCalculatorService  {
  def orderingFactory[T : Ordering](compField: LeagueTeamStatAccumulator => T): Ordering[LeagueTeamStatAccumulator] =
    new Ordering[LeagueTeamStatAccumulator] {
      override def compare(x: LeagueTeamStatAccumulator, y: LeagueTeamStatAccumulator): Int = {
        if(compField(x) > compField(y)) return 1
        if(compField(x) < compField(y)) return -1
        if((x.scored - x.missed) != (y.scored - y.missed)) return (x.scored - x.missed).compareTo(y.scored - y.missed)
        0
      }
    }

  /**
   * In case of any errors (e.g. https://github.com/Blackmorse/hat-all-stats/issues/480) will return Left
   */
  def calculateSafe(leagueFixture: LeagueFixtures,
                tillRound: Option[Int] = None,
                sortingField: String = "points",
                sortingDirection: SortingDirection = Desc): Either[Unit, LeagueUnitTeamStatHistoryInfo] = {
    try {
      Right(calculate(leagueFixture, tillRound, sortingField, sortingDirection))
    } catch {
      case _: Throwable => Left(())
    }
  }

  private def calculate(leagueFixture: LeagueFixtures, tillRound: Option[Int] = None, sortingField: String = "points", sortingDirection: SortingDirection = Desc): LeagueUnitTeamStatHistoryInfo = {
    val ordering = sortingField match {
      case "points" => orderingFactory(_.points)
      case "teamName" => orderingFactory(_.teamName)
      case "scored" => orderingFactory(_.scored)
      case "missed" => orderingFactory(_.missed)
      case "win" => orderingFactory(_.win)
      case "draw" => orderingFactory(_.draw)
      case "lost" => orderingFactory(_.lost)
    }

    val accumulatorMap = mutable.HashMap[Long, LeagueTeamStatAccumulator]()

    val teamPositions = for (round <- 1 to tillRound.getOrElse(14)) yield {
      for(matc <- leagueFixture.matches.filter(_.matchRound == round)) {
        val homeTeamAccumulator = accumulatorMap.getOrElseUpdate(matc.homeTeam.homeTeamId, LeagueTeamStatAccumulator(matc.homeTeam))
        val awayTeamAccumulator = accumulatorMap.getOrElseUpdate(matc.awayTeam.awayTeamId, LeagueTeamStatAccumulator(matc.awayTeam))

        if(matc.homeGoals.isDefined) {
          accumulate(homeTeamAccumulator, awayTeamAccumulator, matc)
        }
      }

      val accumulators = accumulatorMap.toSeq.map(_._2).sorted(ordering)

      val accumulatorDirection = sortingDirection match {
        case Desc => accumulators.reverse
        case Asc => accumulators
      }

      accumulatorDirection.zipWithIndex
        .map{case (accumulator, index) =>
          LeagueUnitTeamStat(round = round,
            position = index + 1,
            teamId = accumulator.teamId,
            teamName = accumulator.teamName,
            games = accumulator.games,
            scored = accumulator.scored,
            missed = accumulator.missed,
            win = accumulator.win,
            draw = accumulator.draw,
            lost = accumulator.lost,
            points = accumulator.points)
        }
    }

    val teamPositionsReverse = teamPositions.reverse

    val previousPositionsOpt = teamPositionsReverse.tail.headOption
    val teamsLastRoundWithPositionsDiff = if(previousPositionsOpt.isEmpty) {
      teamPositionsReverse.head.map(luts => LeagueUnitTeamStatsWithPositionDiff(0, luts))
    } else {
      teamPositionsReverse.head.map(luts => {
        val previousPosition = previousPositionsOpt.get.find(_.teamId == luts.teamId).get.position
        val positionChange = luts.position - previousPosition
        LeagueUnitTeamStatsWithPositionDiff(positionChange, luts)
      })
    }

    LeagueUnitTeamStatHistoryInfo(teamsLastRoundWithPositionsDiff = teamsLastRoundWithPositionsDiff,
      positionsHistory = teamPositionsReverse.flatten)
  }

  private def accumulate(homeTeamAccumulator: LeagueTeamStatAccumulator, awayTeamAccumulator: LeagueTeamStatAccumulator, matc: Match): Unit = {
    homeTeamAccumulator.games += 1
    awayTeamAccumulator.games += 1

    homeTeamAccumulator.scored += matc.homeGoals.get
    awayTeamAccumulator.scored += matc.awayGoals.get

    homeTeamAccumulator.missed += matc.awayGoals.get
    awayTeamAccumulator.missed += matc.homeGoals.get

    if (matc.homeGoals.get > matc.awayGoals.get) {
      homeTeamAccumulator.win += 1
      awayTeamAccumulator.lost += 1

      homeTeamAccumulator.points += 3
    } else if (matc.homeGoals.get < matc.awayGoals.get) {
      awayTeamAccumulator.win += 1
      homeTeamAccumulator.lost += 1

      awayTeamAccumulator.points += 3
    } else {
      homeTeamAccumulator.draw += 1
      awayTeamAccumulator.draw += 1

      homeTeamAccumulator.points += 1
      awayTeamAccumulator.points += 1
    }
  }
}

case class LeagueTeamStatAccumulator(var teamId: Long, var teamName: String, var games: Int, var scored: Int, var missed: Int,
                                             var win: Int, var draw: Int, var lost: Int, var points: Int)

object LeagueTeamStatAccumulator {
  def apply(homeTeam: HomeTeam): LeagueTeamStatAccumulator =
    LeagueTeamStatAccumulator(homeTeam.homeTeamId, homeTeam.homeTeamName, 0 ,0 ,0 ,
      0, 0 ,0 ,0)

  def apply(awayTeam: AwayTeam): LeagueTeamStatAccumulator =
    LeagueTeamStatAccumulator(awayTeam.awayTeamId, awayTeam.awayTeamName, 0 ,0 ,0 ,
      0, 0 ,0 ,0)
}
