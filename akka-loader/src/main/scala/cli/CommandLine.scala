package cli

import org.rogach.scallop._

sealed trait CliConfig

case class ScheduleConfig(from: Option[String], entity: String, lastMatchWindow: Int) extends CliConfig
case class LoadConfig(leagues: List[String], entity: String, lastMatchWindow: Int) extends CliConfig
case class TeamRankingsConfig(league: Option[String]) extends CliConfig
case class LoadScheduledConfig(entity: String, lastMatchWindow: Int) extends CliConfig


class CommandLine(arguments: Array[String]) extends ScallopConf(arguments) {
  class EntitySubcommand(name: String) extends Subcommand(name) {
    val entity = opt[String](required = true, validate = ent => ent == "league" || ent == "cup")
    val lastMatchWindow = opt[Int](required = false, default = Some(4))
  }

  object schedule extends EntitySubcommand("schedule") {
    val from: ScallopOption[String] = opt[String](required = false)
  }
  object load extends EntitySubcommand("load") {
    val leagues: ScallopOption[List[String]] = opt[List[String]](required = true)
  }
  object teamRankings extends Subcommand("teamRankings") {
    val league = opt[String](required = false)
  }
  object loadScheduled extends Subcommand("loadScheduled") {
    val lastMatchWindow = opt[Int](required = false, default = Some(4))
    val entity = opt[String](required = true, validate = ent => ent == "league" || ent == "cup" || ent == "auto")
  }
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

