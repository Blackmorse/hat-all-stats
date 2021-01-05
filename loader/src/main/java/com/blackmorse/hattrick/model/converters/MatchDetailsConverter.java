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

        HomeAwayTeam currentTeam;
        HomeAwayTeam oppositeTeam;
        Long oppositeTeamId;
        String oppositeTeamName;
        Integer goals;
        Integer enemyGoals;
        MatchDetails.IsHomeMatch isHomeMatch;

        if (homeTeamId.equals(teamWithMatch.getTeam().getId())) {
            isHomeMatch = MatchDetails.IsHomeMatch.HOME;
            currentTeam = matchDetails.getMatch().getHomeTeam();
            oppositeTeam = matchDetails.getMatch().getAwayTeam();
            oppositeTeamId = matchDetails.getMatch().getAwayTeam().getAwayTeamId();
            oppositeTeamName = matchDetails.getMatch().getAwayTeam().getAwayTeamName();
            goals = matchDetails.getMatch().getHomeTeam().getHomeGoals();
            enemyGoals = matchDetails.getMatch().getAwayTeam().getAwayGoals();
        } else {
            isHomeMatch = MatchDetails.IsHomeMatch.AWAY;
            currentTeam = matchDetails.getMatch().getAwayTeam();
            oppositeTeam = matchDetails.getMatch().getHomeTeam();
            oppositeTeamId = matchDetails.getMatch().getHomeTeam().getHomeTeamId();
            oppositeTeamName = matchDetails.getMatch().getHomeTeam().getHomeTeamName();
            goals = matchDetails.getMatch().getAwayTeam().getAwayGoals();
            enemyGoals = matchDetails.getMatch().getHomeTeam().getHomeGoals();
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

                .isHomeMatch(isHomeMatch)
                .goals(goals)
                .enemyGoals(enemyGoals)

                .soldTotal(matchDetails.getMatch().getArena().getSoldTotal())

                .formation(currentTeam.getFormation())
                .tacticType(currentTeam.getTacticType())
                .tacticSkill(currentTeam.getTacticSkill())
                .ratingMidfield(currentTeam.getRatingMidfield())
                .ratingLeftDef(currentTeam.getRatingLeftDef())
                .ratingMidDef(currentTeam.getRatingMidDef())
                .ratingRightDef(currentTeam.getRatingRightDef())
                .ratingLeftAtt(currentTeam.getRatingLeftAtt())
                .ratingMidAtt(currentTeam.getRatingMidAtt())
                .ratingRightAtt(currentTeam.getRatingRightAtt())
                .ratingIndirectSetPiecesDef(currentTeam.getRatingIndirectSetPiecesDef())
                .ratingIndirectSetPiecesAtt(currentTeam.getRatingIndirectSetPiecesAtt())

                .oppositeTeamId(oppositeTeamId)
                .oppositeTeamName(oppositeTeamName)
                .oppositeFormation(oppositeTeam.getFormation())
                .oppositeTacticType(oppositeTeam.getTacticType())
                .oppositeTacticSkill(oppositeTeam.getTacticSkill())
                .oppositeRatingMidfield(oppositeTeam.getRatingMidfield())
                .oppositeRatingLeftDef(oppositeTeam.getRatingLeftDef())
                .oppositeRatingMidDef(oppositeTeam.getRatingMidDef())
                .oppositeRatingRightDef(oppositeTeam.getRatingRightDef())
                .oppositeRatingLeftAtt(oppositeTeam.getRatingLeftAtt())
                .oppositeRatingMidAtt(oppositeTeam.getRatingMidAtt())
                .oppositeRatingRightAtt(oppositeTeam.getRatingRightAtt())
                .oppositeRatingIndirectSetPiecesDef(oppositeTeam.getRatingIndirectSetPiecesDef())
                .oppositeRatingIndirectSetPiecesAtt(oppositeTeam.getRatingIndirectSetPiecesAtt())
                .build();
    }
}
