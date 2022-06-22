package tests

import chpp.worlddetails.models.WorldDetails
import hattid.{CommonData, CupSchedule, ScheduleEntry}

import java.util.{Calendar, Date}

object AllCountriesTest {

  def testNumberOfCountries(worldDetails: WorldDetails): Unit = {
    if (CommonData.higherLeagueMap.size != worldDetails.leagueList.size) {
      throw new Exception(s"Expected ${CommonData.higherLeagueMap.size} countries, but got ${worldDetails.leagueList.size}")
    }
  }

  def testCupSchedule(worldDetails: WorldDetails): Unit = {

    val schedule = CupSchedule.normalizeCupScheduleToDayOfWeek(CupSchedule.seq, Calendar.MONDAY)
      .sortBy(_.leagueId)

    val dayLightSavingOffset = if (CupSchedule.isSummerTimeNow()) 0L else 1000L * 60 * 60

    val worldDetailsSchedule = CupSchedule.normalizeCupScheduleToDayOfWeek(
          worldDetails.leagueList
          .map(league => ScheduleEntry(league.leagueId, new Date(league.cupMatchDate.get.getTime + dayLightSavingOffset))),
        Calendar.MONDAY)
      .sortBy(_.leagueId)

    val changes = schedule.zip(worldDetailsSchedule)
      .filter{case (original, worldDetails) => original.date != worldDetails.date}
      .map{case (original, worldDetails) =>
        s"Cup schedule has been changed. Backup: $original. World details: $worldDetails"}

    if (changes.nonEmpty) {
      throw new Exception(s"${changes.head}.\n ... \n Total ${changes.size}")
    }
  }

  def testFirstAndLastLeague(worldDetails: WorldDetails): Unit = {
    val schedule = CupSchedule.normalizeCupScheduleToDayOfWeek(worldDetails.leagueList
      .map(league => ScheduleEntry(league.leagueId, league.seriesMatchDate)), Calendar.THURSDAY)
      .sortBy(_.date)

    if (schedule.head.leagueId != 1000) {
      throw new Exception(s"Now first league is ${schedule.head} instead of Hattrick International!")
    }
    if (schedule.last.leagueId != 100) {
      throw new Exception(s"Now last league is ${schedule.last} instead of El Salvador!")
    }
  }

  def testDivisionLevels(worldDetails: WorldDetails): Unit = {
    if (worldDetails.leagueList.exists(_.numberOfLevels > 10)) {
      throw new Exception(s"There is a leagues with more than 10 divisionLevels: " +
        s"${worldDetails.leagueList.filter(_.numberOfLevels > 10).map(_.leagueId).mkString(",")}")
    }
  }
}
