package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.HattrickApi;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.matchesarchive.model.MatchesArchive;
import com.blackmorse.hattrick.api.nationalteamdetails.model.NationalTeamDetails;
import com.blackmorse.hattrick.api.search.model.Result;
import com.blackmorse.hattrick.api.search.model.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Hattrick {
    private static Map<Integer, String> arabNumbers = new HashMap<>();

    static {
        arabNumbers.put(2, "II");
        arabNumbers.put(3, "III");
        arabNumbers.put(4, "IV");
        arabNumbers.put(5, "V");
        arabNumbers.put(6, "VI");
        arabNumbers.put(7, "VII");
        arabNumbers.put(8, "VIII");
        arabNumbers.put(9, "IX");
        arabNumbers.put(10, "X");
        arabNumbers.put(11, "XI");
        arabNumbers.put(12, "XII");
        arabNumbers.put(13, "XIII");
        arabNumbers.put(14, "XIV");
        arabNumbers.put(15, "XV");
    }

    private final HattrickApi hattrickApi;


    @Autowired
    public Hattrick(HattrickApi hattrickApi) {
        this.hattrickApi = hattrickApi;
    }

    public NationalTeamDetails getNationalTeamDetails(Integer countryTeamId) {
        return hattrickApi.nationalTeamDetails().teamId(countryTeamId).execute();
    }

    public LeagueDetails getLeagueUnitByName(Integer leagueId, String leagueName) {
        Search search = hattrickApi.search().searchType(3).searchLeagueId(leagueId).searchString(leagueName).execute();
        return hattrickApi.leagueDetails().leagueLevelUnitId(search.getSearchResults().get(0).getResultId()).execute();
    }

    public List<Long> getLeagueUnitIdsForLevel(int leagueId, int level) {
        List<Long> result = new ArrayList<>();

        Search leagueSearch = hattrickApi.search().searchLeagueId(leagueId).searchType(3).searchString(arabNumbers.get(level) + ".").execute();
        leagueSearch.getSearchResults().stream().map(Result::getResultId).forEach(result::add);

        for (int page = 1; page < leagueSearch.getPages(); page++) {
            Search leagueSearchPage = hattrickApi.search().searchLeagueId(leagueId).pageIndex(page).searchType(3).searchString(arabNumbers.get(level) + ".").execute();

            leagueSearchPage.getSearchResults().stream().map(Result::getResultId).forEach(result::add);
        }
        return result;
    }

    public LeagueDetails getLeagueUnitById(long id) {
        return hattrickApi.leagueDetails().leagueLevelUnitId(id).execute();
    }

    public MatchesArchive getArchiveMatches(Long teamId, Integer season) {
        return hattrickApi.matchesArchive().season(season).teamId(teamId).execute();
    }
}
