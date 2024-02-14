package cli

import org.rogach.scallop.{ScallopConf, Subcommand}

sealed trait CliConfig

case class ScheduleConfig(from: Option[String], entity: String, lastMatchWindow: Int) extends CliConfig
case class LoadConfig(leagues: List[String], entity: String, lastMatchWindow: Int) extends CliConfig
case class TeamRankingsConfig(league: Option[String]) extends CliConfig
case class LoadScheduledConfig(entity: String, lastMatchWindow: Int) extends CliConfig


class CommandLine(arguments: Array[String]) extends ScallopConf(arguments) {
  class EntitySubcommand(name: String) extends Subcommand(name) {
    val entity = opt[String](required = true, validate = ent => ent == "league" || ent == "cup")
    val lastMatchWindow = opt[Int](required = false, default = Some(7))
  }

  val schedule = new EntitySubcommand("schedule") {
    val from = opt[String](required = false)
  }
  val load = new EntitySubcommand("load") {
    val leagues = opt[List[String]](required = true)
  }
  val teamRankings = new Subcommand("teamRankings") {
    val league = opt[String](required = false)
  }
  val loadScheduled = new EntitySubcommand("loadScheduled") {}
  addSubcommand(schedule)
  addSubcommand(load)
  addSubcommand(loadScheduled)
  verify()

  def toCliConfig: CliConfig = {
    this.subcommand match {
      case Some(this.schedule) => ScheduleConfig(this.schedule.from.toOption, this.schedule.entity(), this.schedule.lastMatchWindow())
      case Some(this.load) => LoadConfig(this.load.leagues(), this.load.entity(), this.schedule.lastMatchWindow())
      case Some(this.teamRankings) => TeamRankingsConfig(this.teamRankings.league.toOption)
      case Some(this.loadScheduled) => LoadScheduledConfig(this.loadScheduled.entity(), this.loadScheduled.lastMatchWindow())
    }
  }
}

