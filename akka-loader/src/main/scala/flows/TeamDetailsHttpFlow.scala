package flows

import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.TeamDetails

object TeamDetailsHttpFlow extends AbstractHttpFlow[TeamDetailsRequest, TeamDetails]
