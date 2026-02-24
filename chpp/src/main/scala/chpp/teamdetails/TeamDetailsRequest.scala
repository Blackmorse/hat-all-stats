package chpp.teamdetails

import chpp.AbstractRequest
import chpp.teamdetails.models.TeamDetails

case class TeamDetailsRequest(teamId: Option[Long] = None,
                              userId: Option[Int] = None,
                              includeDomesticFlags: Option[Boolean] = None,
                              includeFlags: Option[Boolean] = None,
                              includeSupporters: Option[Boolean] = None) extends AbstractRequest[TeamDetails]("teamdetails", "3.4",
  "teamID" -> teamId,
  "userID" -> userId,
  "includeDomesticFlags" -> includeDomesticFlags,
  "includeFlags" -> includeFlags,
  "includeSupporters" -> includeSupporters)
