package databases.requests.playerstats.player

import databases.requests.OrderingKeyPath
import databases.requests.model.Roles
import databases.sqlbuilder.SqlBuilder
import models.web.{PlayersParameters, RestStatisticsParameters}

trait ClickhousePlayerAggregateRoundRequest[T] extends ClickhousePlayerRequest[T] {

  override def buildSql(orderingKeyPath: OrderingKeyPath,
               parameters: RestStatisticsParameters,
               playersParameters: PlayersParameters,
               role: Option[String],
               round: Int): SqlBuilder = {
      SqlBuilder(oneRoundSql)

        .where
          .applyParameters(parameters)
          .applyParameters(orderingKeyPath)
        .having
          .round.lessEqual(round)
          .role(role.map(Roles.reverseMapping))
          .nationality(playersParameters.nationality)
          .age.greaterEqual(playersParameters.minAge.map(_ * 112))
          .age.lessEqual(playersParameters.maxAge.map(_ * 112 + 111))
        .sortBy(parameters.sortBy)
  }

}
