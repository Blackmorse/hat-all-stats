package loadergraph.matchlineup

import chpp.matchdetails.models.MatchDetails
import chpp.matchlineup.models.MatchLineup
import models.stream.Match

case class StreamMatchDetailsWithLineup(matc: Match,
                                        matchDetails: MatchDetails,
                                        matchLineup: MatchLineup)
