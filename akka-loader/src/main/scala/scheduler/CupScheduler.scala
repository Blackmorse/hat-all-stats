package scheduler

import executors.TaskExecutorActor.{ScheduleFinished, ScheduleTask}
import akka.actor.ActorRef
import chpp.worlddetails.models.WorldDetails
import hattid.CupSchedule

import java.util.{Calendar, Date}

class CupScheduler(worldDetails: WorldDetails,
                   taskExecutorActor: ActorRef) extends AbstractScheduler(worldDetails) {
  override def schedule(): Unit = {
    CupSchedule.normalizeCupScheduleToDayOfWeek(CupSchedule.seq, Calendar.MONDAY)
      .filter(_.date.after(new Date()))
      .map(scheduleEntry => {
        val scheduledDate = new Date(scheduleEntry.date.getTime + threeHoursMs + 1000L * 60 * 60) //TODO quick fix for winter time!
        ScheduleTask(scheduleEntry.leagueId, scheduledDate)
      })
      .foreach(task => taskExecutorActor ! task)

    taskExecutorActor ! ScheduleFinished
  }

  override protected def scheduleFrom(leagueId: Int): Unit = {
    CupSchedule.normalizeCupScheduleToDayOfWeek(CupSchedule.seq, Calendar.MONDAY)
      .sortBy(_.date)
      .dropWhile(_.leagueId != leagueId)
      .map(scheduleEntry => {
        val scheduledDate = new Date(scheduleEntry.date.getTime + threeHoursMs + 1000L * 60 * 60) //TODO quick fix for winter time!
        ScheduleTask(scheduleEntry.leagueId, scheduledDate)
      })
      .foreach(task => taskExecutorActor ! task)

    taskExecutorActor ! ScheduleFinished
  }

  override protected def load(leagueIds: Seq[Int]): Unit = {
    leagueIds.foreach(leagueID => taskExecutorActor ! ScheduleTask(leagueID, new Date()))
  }
}
