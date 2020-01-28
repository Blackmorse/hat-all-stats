package models.web

import play.api.mvc.QueryStringBindable

abstract class StatsType

case class MultiplyRoundsType(function: String) extends StatsType {
  override def toString: String = function
}

object Avg extends MultiplyRoundsType("avg")

object Max extends MultiplyRoundsType("max")

case class Round(round: Int) extends StatsType {
  override def toString: String = round.toString
}

object StatsType {
  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[StatsType] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, StatsType]] = {
      stringBinder.bind("statType", params)
        .map(typeEither => typeEither.flatMap{
          case "avg" => Right(Avg)
          case "max" => Right(Max)
          case "statRound" =>
            stringBinder.bind("statRoundNumber", params)
              .map(statRoundNumberEither => statRoundNumberEither.flatMap(statRoundNumber => {
                if(statRoundNumber forall Character.isDigit)
                  Right(Round(statRoundNumber.toInt): StatsType)
                else
                  Left("Unable to parse")
              })).getOrElse(Left("Unable to parse"))
          case _ => Left("Unable to Parse")
        })
    }

    override def unbind(key: String, value: StatsType): String = value match {
      case Avg => stringBinder.unbind("statType", "avg")
      case Max => stringBinder.unbind("statType", "max")
      case Round(num) => stringBinder.unbind("statType", "statRound") + "&" + stringBinder.unbind("statRoundNumber", num.toString)
    }
  }
}
