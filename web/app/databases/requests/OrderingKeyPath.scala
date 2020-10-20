package databases.requests

case class OrderingKeyPath(season: Option[Int] = None,
                           leagueId: Option[Int] = None,
                           divisionLevel: Option[Int] = None,
                           leagueUnitId: Option[Long] = None,
                           teamId: Option[Long] = None
                          )
