package scheduler

import executors.TaskExecutorActor.{ScheduleFinished, ScheduleTask}
import akka.actor.ActorRef
import chpp.worlddetails.models.{League, WorldDetails}
import hattid.CupSchedule.normalizeCupScheduleToDayOfWeek
import hattid.ScheduleEntry
import scheduler.LeagueScheduler.countriesToMinutesOffset

import java.util.{Calendar, Date}

object LeagueScheduler {

  val countriesToMinutesOffset: Map[Int, Long] = Map(
    24 -> 90L,  //Poland
    4 -> 30L,   //Italy
    36 -> 60L,  //Spain
    46 -> 135L, //Switzerland
    3 -> 30L    //Germany
  )
}

class LeagueScheduler(worldDetails: WorldDetails,
                     taskExecutorActor: ActorRef) extends AbstractScheduler(worldDetails) {
  override def schedule(): Unit = {
    val leagues = worldDetails.leagueList
      .filter(_.matchRound <= 14)

    val tasks = leagues
      .filter(league => league.seriesMatchDate.after(new Date()))
      .map(league => {
        val minutesOffset  = countriesToMinutesOffset.getOrElse(league.leagueId, 0L)

        val scheduledDate = new Date(league.seriesMatchDate.getTime + threeHoursMs
          + minutesOffset * 60 * 1000)
        ScheduleTask(league.leagueId, scheduledDate)
      })

    tasks.foreach(task => taskExecutorActor ! task)

    taskExecutorActor ! ScheduleFinished
  }


  override protected def loadIds(leagueIds: Seq[Int]): Unit = {
    leagueIds.foreach(leagueID => taskExecutorActor ! ScheduleTask(leagueID, new Date()))
  }


  override protected def scheduleFrom(leagueId: Int): Unit = {
    val (firstLeague, lastLeague) = firstAndLastLeagues(worldDetails)

    val matchesAlreadyFinished = firstLeague.seriesMatchDate.after(new Date()) &&
      lastLeague.seriesMatchDate.after(new Date()) && lastLeague.seriesMatchDate.after(firstLeague.seriesMatchDate)

    val previousWeekMs = if (matchesAlreadyFinished) 1000L * 3600 * 24 * 7 else 0L
    worldDetails.leagueList
      .map(league => {
        val minutesOffset: Long  = countriesToMinutesOffset.getOrElse(league.leagueId, 0)

        val date = if (league.seriesMatchDate.after(lastLeague.seriesMatchDate)) {
          new Date(league.seriesMatchDate.getTime - 1000L * 3600 * 24 * 7
            + threeHoursMs + (minutesOffset * 60 * 1000))
        } else {
          new Date(league.seriesMatchDate.getTime + threeHoursMs + + minutesOffset * 60 * 1000)
        }

        ScheduleTask(league.leagueId, new Date(date.getTime - previousWeekMs))
      })
      .sortBy(_.time)
      .dropWhile(_.leagueId != leagueId)
      .foreach(taskExecutorActor ! _)

    taskExecutorActor ! ScheduleFinished
  }

  private def firstAndLastLeagues(worldDetails: WorldDetails): (League, League) = {
    val seriesMatches = worldDetails.leagueList.map(league => ScheduleEntry(league.leagueId, league.seriesMatchDate))

    val normalizedAndSorted = normalizeCupScheduleToDayOfWeek(seriesMatches, Calendar.THURSDAY)
      .sortBy(_.date)

    val first = worldDetails.leagueList.find(_.leagueId == normalizedAndSorted.head.leagueId).get
    val last  = worldDetails.leagueList.find(_.leagueId == normalizedAndSorted.last.leagueId).get
    (first, last)
  }

  override def loadScheduled(): Unit = {
    val (firstLeague, lastLeague) = firstAndLastLeagues(worldDetails)

    val matchesAlreadyFinished = firstLeague.seriesMatchDate.after(new Date()) &&
      lastLeague.seriesMatchDate.after(new Date()) && lastLeague.seriesMatchDate.after(firstLeague.seriesMatchDate)

    val previousWeekMs = if (matchesAlreadyFinished) 1000L * 3600 * 24 * 7 else 0L
    worldDetails.leagueList
      .filter(_.matchRound < 16) // check at the end of the season!
      .map(league => {
        val minutesOffset: Long = countriesToMinutesOffset.getOrElse(league.leagueId, 0)

        val date = if (league.seriesMatchDate.after(lastLeague.seriesMatchDate)) {
          new Date(league.seriesMatchDate.getTime - 1000L * 3600 * 24 * 7
            + threeHoursMs + (minutesOffset * 60 * 1000))
        } else {
          new Date(league.seriesMatchDate.getTime + threeHoursMs + +minutesOffset * 60 * 1000)
        }

        ScheduleTask(league.leagueId, new Date(date.getTime - previousWeekMs))
      })
      .filter(_.time.before(new Date()))
      .sortBy(_.time)
      .foreach(taskExecutorActor ! _)
  }
}
