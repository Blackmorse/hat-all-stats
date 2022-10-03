package tests

import chpp.worlddetails.models.WorldDetails
import hattid.{CommonData, CupSchedule, ScheduleEntry}

import java.util.{Calendar, Date}

object AllCountriesTest {

  def testNumberOfCountries(worldDetails: WorldDetails): Option[String] = {
    if (CommonData.higherLeagueMap.size != worldDetails.leagueList.size) {
      Some(s"Expected ${CommonData.higherLeagueMap.size} countries, but got ${worldDetails.leagueList.size}")
    } else {
      None
    }
  }

  def testCupSchedule(worldDetails: WorldDetails): Option[String] = {

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
      Some(s"${changes.head}.\n ... \n Total ${changes.size}")
    } else {
      None
    }
  }

  def testFirstAndLastLeague(worldDetails: WorldDetails): Option[String] = {
    val schedule = CupSchedule.normalizeCupScheduleToDayOfWeek(worldDetails.leagueList
      .map(league => ScheduleEntry(league.leagueId, league.seriesMatchDate)), Calendar.THURSDAY)
      .sortBy(_.date)

    if (schedule.head.leagueId != 1000) {
      Some(s"Now first league is ${schedule.head} instead of Hattrick International!")
    } else if (schedule.last.leagueId != 100) {
      Some(s"Now last league is ${schedule.last} instead of El Salvador!")
    } else {
      None
    }
  }

  def testDivisionLevels(worldDetails: WorldDetails): Option[String] = {
    if (worldDetails.leagueList.exists(_.numberOfLevels > 10)) {
      Some(s"There is a leagues with more than 10 divisionLevels: " +
        s"${worldDetails.leagueList.filter(_.numberOfLevels > 10).map(_.leagueId).mkString(",")}")
    } else {
      None
    }
  }
}
