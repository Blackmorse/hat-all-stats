package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.*;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.model.TeamWithMatchAndPlayers;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.LeagueUnit;
import com.blackmorse.hattrick.promotions.model.PromoteTeam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class HattrickService {
    private final Hattrick hattrick;
    private final LeagueUnitsLoader leagueUnitsLoader;
    private final MatchLoader matchLoader;
    private final PlayersLoader playersLoader;
    private final TeamLoader teamLoader;

    public HattrickService(Hattrick hattrick,
                           LeagueUnitsLoader leagueUnitsLoader,
                           MatchLoader matchLoader,
                           PlayersLoader playersLoader,
                           TeamLoader teamLoader) {
        this.hattrick = hattrick;
        this.leagueUnitsLoader = leagueUnitsLoader;
        this.matchLoader = matchLoader;
        this.playersLoader = playersLoader;
        this.teamLoader = teamLoader;
    }

    public List<LeagueUnit> getAllLeagueUnitIdsForCountry(String countryName) {
        return leagueUnitsLoader.load(countryName);
    }

    public List<TeamWithMatchDetails> getLastMatchDetails(List<LeagueUnit> leagueUnits) {
        return matchLoader.getLastMatchDetails(leagueUnits);
    }

    public List<TeamWithMatchAndPlayers> getPlayersFromTeam(List<TeamWithMatchDetails> teamWithMatchDetails) {
        return playersLoader.getPlayersFromTeam(teamWithMatchDetails);
    }

    public League getLeagueByCountryName(String countryName) {
        return hattrick.getWorldDetails().getLeagueList().stream()
                .filter(league -> league.getLeagueName().equals(countryName)).findFirst().get();
    }

    public List<PromoteTeam> getPromoteTeams(List<LeagueUnit> leagueUnits) {
        return teamLoader.getPromoteTeams(leagueUnits);
    }

    public List<PromoteTeam> getHistoryPromoteTeams(List<LeagueUnit> leagueUnits, Integer season) {
        return teamLoader.getHistoryPromoteTeams(leagueUnits, season);
    }
}