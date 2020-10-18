package databases.requests

case class OrderingKeyPath(leagueId: Option[Int] = None,
                           divisionLevel: Option[Int] = None,
                           leagueUnitId: Option[Long] = None,
                           teamId: Option[Long] = None
                          )
