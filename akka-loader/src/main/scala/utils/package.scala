import chpp.commonmodels.MatchType
import chpp.worlddetails.models.League

package object utils {
  def realRound(matchType: MatchType.Value, league: League): Int =
    if (matchType == MatchType.LEAGUE_MATCH) league.matchRound - 1
    else if (matchType == MatchType.CUP_MATCH) league.matchRound
    else throw new IllegalArgumentException(matchType.toString)
}
