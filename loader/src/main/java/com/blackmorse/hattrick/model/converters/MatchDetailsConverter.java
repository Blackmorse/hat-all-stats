package com.blackmorse.hattrick.model.converters;

import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.model.TeamWithMatch;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import org.springframework.stereotype.Component;

@Component
public class MatchDetailsConverter {
    public MatchDetails convert(TeamWithMatchDetails teamWithMatchDetails) {
        TeamWithMatch teamWithMatch = teamWithMatchDetails.getTeamWithMatch();
        com.blackmorse.hattrick.api.matchdetails.model.MatchDetails matchDetails = teamWithMatchDetails.getMatchDetails();

        Long homeTeamId = matchDetails.getMatch().getHomeTeam().getHomeTeamId();

        HomeAwayTeam homeAwayTeam;
        if (homeTeamId.equals(teamWithMatch.getTeam().getId())) {
            homeAwayTeam = matchDetails.getMatch().getHomeTeam();
        } else {
            homeAwayTeam = matchDetails.getMatch().getAwayTeam();
        }

        return MatchDetails.builder()
                .season(teamWithMatch.getMatch().getSeason())
                .leagueId(teamWithMatch.getTeam().getLeagueUnit().getLeague().getId())
                .divisionLevel(teamWithMatch.getTeam().getLeagueUnit().getLevel())
                .leagueUnitId(teamWithMatch.getTeam().getLeagueUnit().getId())
                .leagueUnitName(teamWithMatch.getTeam().getLeagueUnit().getName())
                .teamId(teamWithMatch.getTeam().getId())
                .teamName(teamWithMatch.getTeam().getName())
                .date(teamWithMatch.getMatch().getDate())
                .round(teamWithMatch.getMatch().getRound())
                .matchId(teamWithMatch.getMatch().getId())

                .formation(homeAwayTeam.getFormation())
                .tacticType(homeAwayTeam.getTacticType())
                .tacticSkill(homeAwayTeam.getTacticSkill())
                .ratingMidfield(homeAwayTeam.getRatingMidfield())
                .ratingLeftDef(homeAwayTeam.getRatingLeftDef())
                .ratingMidDef(homeAwayTeam.getRatingMidDef())
                .ratingRightDef(homeAwayTeam.getRatingRightDef())
                .ratingLeftAtt(homeAwayTeam.getRatingLeftAtt())
                .ratingMidAtt(homeAwayTeam.getRatingMidAtt())
                .ratingRightAtt(homeAwayTeam.getRatingRightAtt())
                .ratingIndirectSetPiecesDef(homeAwayTeam.getRatingIndirectSetPiecesDef())
                .ratingIndirectSetPiecesAtt(homeAwayTeam.getRatingIndirectSetPiecesAtt())
                .build();
    }
}
