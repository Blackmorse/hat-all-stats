package service

import com.blackmorse.hattrick.api.leaguefixtures.model.{LeagueFixtures, Match}
import com.blackmorse.hattrick.model.common.{AwayTeam, HomeTeam}
import javax.inject.Singleton
import models.web.{Asc, Desc, SortingDirection}
import play.api.libs.json.Json

import scala.collection.mutable
import collection.JavaConverters._
import Ordering.Implicits._

@Singleton
class LeagueUnitCalculatorService  {
  def orderingFactory[T : Ordering](compField: LeagueTeamStatAccumulator => T): Ordering[LeagueTeamStatAccumulator] =
    new Ordering[LeagueTeamStatAccumulator] {
      override def compare(x: LeagueTeamStatAccumulator, y: LeagueTeamStatAccumulator): Int = {
        if(compField(x) > compField(y)) return -1
        if(compField(x) < compField(y)) return 1
        if((x.scored - x.missed) != (y.scored - y.missed)) return (x.scored - x.missed).compareTo(y.scored - y.missed)
        0
      }
    }

  def calculate(leagueFixture: LeagueFixtures, tillRound: Option[Int] = None, sortingField: String = "points", sortingDirection: SortingDirection = Desc): Seq[LeagueUnitTeamStatsWithPositionDiff] = {
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
      for(matc <- leagueFixture.getMatches.asScala.filter(_.getMatchRound == round)) {
        val homeTeamAccumulator = accumulatorMap.getOrElseUpdate(matc.getHomeTeam.getHomeTeamId, LeagueTeamStatAccumulator(matc.getHomeTeam))
        val awayTeamAccumulator = accumulatorMap.getOrElseUpdate(matc.getAwayTeam.getAwayTeamId, LeagueTeamStatAccumulator(matc.getAwayTeam))

        if(matc.getHomeGoals != null) {
          accumulate(homeTeamAccumulator, awayTeamAccumulator, matc)
        }
      }

      val accumulators = accumulatorMap.toSeq.map(_._2).sorted(ordering)

      val accumulatorDirection = sortingDirection match {
        case Desc => accumulators.reverse
        case Asc => accumulators
      }

      accumulatorDirection.reverse.zipWithIndex
        .map{case (accumulator, index) =>
          LeagueUnitTeamStat(index + 1, accumulator.teamId, accumulator.teamName, accumulator.games, accumulator.scored, accumulator. missed,
            accumulator.win, accumulator.draw, accumulator.lost, accumulator.points)
        }
    }

    val teamPositionsReverse = teamPositions.reverse
    val previousPositionsOpt = teamPositionsReverse.tail.headOption
    if(previousPositionsOpt.isEmpty) {
      teamPositionsReverse.head.map(luts => LeagueUnitTeamStatsWithPositionDiff(0, luts))
    } else {
      teamPositionsReverse.head.map(luts => {
        val previousPosition = previousPositionsOpt.get.find(_.teamId == luts.teamId).get.position
        val positionChange = luts.position - previousPosition
        LeagueUnitTeamStatsWithPositionDiff(positionChange, luts)
      })
    }
  }

  private def accumulate(homeTeamAccumulator: LeagueTeamStatAccumulator, awayTeamAccumulator: LeagueTeamStatAccumulator, matc: Match): Unit = {
    homeTeamAccumulator.games += 1
    awayTeamAccumulator.games += 1

    homeTeamAccumulator.scored += matc.getHomeGoals
    awayTeamAccumulator.scored += matc.getAwayGoals

    homeTeamAccumulator.missed += matc.getAwayGoals
    awayTeamAccumulator.missed += matc.getHomeGoals

    if (matc.getHomeGoals > matc.getAwayGoals) {
      homeTeamAccumulator.win += 1
      awayTeamAccumulator.lost += 1

      homeTeamAccumulator.points += 3
    } else if (matc.getHomeGoals < matc.getAwayGoals) {
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
    LeagueTeamStatAccumulator(homeTeam.getHomeTeamId, homeTeam.getHomeTeamName, 0 ,0 ,0 ,
      0, 0 ,0 ,0)

  def apply(awayTeam: AwayTeam): LeagueTeamStatAccumulator =
    LeagueTeamStatAccumulator(awayTeam.getAwayTeamId, awayTeam.getAwayTeamName, 0 ,0 ,0 ,
      0, 0 ,0 ,0)
}

case class LeagueUnitTeamStat(position: Int, teamId: Long, teamName: String, games: Int, scored: Int, missed: Int,
                              win: Int, draw: Int, lost: Int, points: Int)

case class LeagueUnitTeamStatsWithPositionDiff(positionDiff: Int, leagueUnitTeamStat: LeagueUnitTeamStat)

object LeagueUnitTeamStat {
  implicit val writes = Json.writes[LeagueUnitTeamStat]
}

object LeagueUnitTeamStatsWithPositionDiff {
  implicit val writes = Json.writes[LeagueUnitTeamStatsWithPositionDiff]
}
