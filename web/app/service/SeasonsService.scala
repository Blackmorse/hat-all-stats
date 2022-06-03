package service

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}

case class DatesRange(min: Date, max: Date)

case class TeamCreatedRanges(season: Int,
                              round: Int,
                              seasonRange: DatesRange,
                              roundRange: DatesRange)

@Singleton
class SeasonsService @Inject()() {
  private val firstSeasonFirstDate = new SimpleDateFormat("yyyy-MM-dd").parse("1997-09-28") //1 season 1 day!

  private val weekMs: Long = 1000L * 3600 * 24 * 7
  private val seasonMs = weekMs * 16

  private def getSeasonAndRoundFromDate(date: Date): (Int, Int) = {
    val season = (date.getTime - firstSeasonFirstDate.getTime) / seasonMs + 1
    val round = ((date.getTime - firstSeasonFirstDate.getTime) % seasonMs) / weekMs + 1
    (season.toInt, round.toInt)
  }

  def getSeasonAndRoundRanges(date: Date): TeamCreatedRanges = {
    val (season, round) = getSeasonAndRoundFromDate(date)

    val firstDayOfSeasonMs = firstSeasonFirstDate.getTime + (season - 1) * seasonMs
    val firstDayOfSeason = new Date(firstDayOfSeasonMs)

    val firstDayOfNextSeason = new Date(firstDayOfSeasonMs + seasonMs)

    val firstDayOfRoundMs = firstDayOfSeasonMs + (round - 1) * weekMs
    val firstDayOfRound = new Date(firstDayOfRoundMs)

    val firstDayOfNextRound = new Date(firstDayOfRoundMs + weekMs)

    TeamCreatedRanges(
      season = season,
      round = round,
      seasonRange = DatesRange(firstDayOfSeason, firstDayOfNextSeason),
      roundRange = DatesRange(firstDayOfRound, firstDayOfNextRound))
  }

  def getWeeksRange(date: Date, weeksNumber: Int): DatesRange = {
    val minDate = new Date(date.getTime - weekMs * weeksNumber)
    val maxDate = new Date(date.getTime + weekMs * weeksNumber)

    DatesRange(minDate, maxDate)
  }
}