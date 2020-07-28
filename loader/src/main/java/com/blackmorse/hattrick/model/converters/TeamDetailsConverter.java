package com.blackmorse.hattrick.model.converters;

import com.blackmorse.hattrick.clickhouse.model.TeamDetails;
import com.blackmorse.hattrick.model.TeamWithMatchAndTeamDetails;
import com.blackmorse.hattrick.model.enums.TrophyTypeId;
import org.springframework.stereotype.Component;

@Component
public class TeamDetailsConverter {
    public TeamDetails convert(TeamWithMatchAndTeamDetails teamWithMatchAndTeamDetails) {
        Integer trophyNumber = Math.toIntExact(teamWithMatchAndTeamDetails.getTeamDetails()
                .getTrophyList().stream().filter(trophy -> !trophy.getTrophyTypeId().equals(TrophyTypeId.TOURNAMENT_WINNER) &&
                                                            !trophy.getTrophyTypeId().equals(TrophyTypeId.STUDY_TOURNNAMENT))
                .count());

        return TeamDetails.builder()
                .season(teamWithMatchAndTeamDetails.getTeamWithMatch().getMatch().getSeason())
                .leagueId(teamWithMatchAndTeamDetails.getTeamWithMatch().getTeam().getLeagueUnit().getLeague().getId())
                .divisionLevel(teamWithMatchAndTeamDetails.getTeamWithMatch().getTeam().getLeagueUnit().getLevel())
                .leagueUnitId(teamWithMatchAndTeamDetails.getTeamWithMatch().getTeam().getLeagueUnit().getId())
                .leagueUnitName(teamWithMatchAndTeamDetails.getTeamWithMatch().getTeam().getLeagueUnit().getName())
                .teamId(teamWithMatchAndTeamDetails.getTeamWithMatch().getTeam().getId())
                .teamName(teamWithMatchAndTeamDetails.getTeamWithMatch().getTeam().getName())
                .round(teamWithMatchAndTeamDetails.getTeamWithMatch().getMatch().getRound())

                .powerRating(teamWithMatchAndTeamDetails.getTeamDetails().getPowerRating().getPowerRating())
                .homeFlags(teamWithMatchAndTeamDetails.getTeamDetails().getFlags().getHomeFlags().size())
                .awayFlags(teamWithMatchAndTeamDetails.getTeamDetails().getFlags().getAwayFlags().size())
                .fanclubSize(teamWithMatchAndTeamDetails.getTeamDetails().getFanclub().getFanclubSize())
                .trophiesNumber(trophyNumber)
                .numberOfVictories(teamWithMatchAndTeamDetails.getTeamDetails().getNumberOfVictories())
                .numberOfUndefeated(teamWithMatchAndTeamDetails.getTeamDetails().getNumberOfUndefeated())
            .build();
    }
}
