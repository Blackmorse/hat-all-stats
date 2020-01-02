package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.HattrickApi;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.nationalteamdetails.model.NationalTeamDetails;
import com.blackmorse.hattrick.api.search.model.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Hattrick {
    private final HattrickApi hattrickApi;

    @Autowired
    public Hattrick(HattrickApi hattrickApi) {
        this.hattrickApi = hattrickApi;
    }

    public NationalTeamDetails getNationalTeamDetails(Integer countryTeamId) {
        return hattrickApi.nationalTeamDetails().teamId(countryTeamId).execute();
    }

    public LeagueDetails getLeagueByName(Integer leagueId, String leagueName) {
        Search search = hattrickApi.search().searchType(3).searchLeagueId(leagueId).searchString(leagueName).execute();
        return hattrickApi.leagueDetails().leagueLevelUnitId(search.getSearchResults().get(0).getResultId()).execute();
    }
}
