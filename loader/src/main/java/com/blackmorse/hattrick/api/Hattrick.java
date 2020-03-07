package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.HattrickApi;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.leaguefixtures.model.LeagueFixtures;
import com.blackmorse.hattrick.api.matchdetails.model.MatchDetails;
import com.blackmorse.hattrick.api.matches.model.Matches;
import com.blackmorse.hattrick.api.matchesarchive.model.MatchesArchive;
import com.blackmorse.hattrick.api.matchlineup.model.MatchLineUp;
import com.blackmorse.hattrick.api.nationalteamdetails.model.NationalTeamDetails;
import com.blackmorse.hattrick.api.players.model.Players;
import com.blackmorse.hattrick.api.search.model.Result;
import com.blackmorse.hattrick.api.search.model.Search;
import com.blackmorse.hattrick.api.teamdetails.model.TeamDetails;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.api.worlddetails.model.WorldDetails;
import com.blackmorse.hattrick.exceptions.HattrickChppException;
import com.blackmorse.hattrick.exceptions.HattrickTransferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Hattrick {
    public static Map<Integer, String> arabToRomans = new HashMap<>();
    public static Map<String, Integer> romansToArab = new HashMap<>();
    public static Map<Integer, Integer> leagueLevelNumberTeams = new HashMap<>();

//    lazy val leagueNumbersMap = Map(1 -> Seq(1),
//    2 -> (1 to 4),
//            3 -> (1 to 16),
//            4 -> (1 to 64),
//            5 -> (1 to 256),
//            6 -> (1 to 1024),
//            7 -> (1 to 1024),
//            8 -> (1 to 2048),
//            9 -> (1 to 2048)

    static {
        leagueLevelNumberTeams.put(1, 1);
        leagueLevelNumberTeams.put(2, 4);
        leagueLevelNumberTeams.put(3, 16);
        leagueLevelNumberTeams.put(4, 64);
        leagueLevelNumberTeams.put(5, 256);
        leagueLevelNumberTeams.put(6, 1024);
        leagueLevelNumberTeams.put(7, 1024);
        leagueLevelNumberTeams.put(8, 2048);
        leagueLevelNumberTeams.put(9, 2048);
    }

    static {
        arabToRomans.put(2, "II");
        arabToRomans.put(3, "III");
        arabToRomans.put(4, "IV");
        arabToRomans.put(5, "V");
        arabToRomans.put(6, "VI");
        arabToRomans.put(7, "VII");
        arabToRomans.put(8, "VIII");
        arabToRomans.put(9, "IX");
        arabToRomans.put(10, "X");
        arabToRomans.put(11, "XI");
        arabToRomans.put(12, "XII");
        arabToRomans.put(13, "XIII");
        arabToRomans.put(14, "XIV");
        arabToRomans.put(15, "XV");
    }

    static {
        romansToArab.put("II", 2);
        romansToArab.put("III", 3);
        romansToArab.put("IV", 4);
        romansToArab.put("V", 5);
        romansToArab.put("VI", 6);
        romansToArab.put("VII", 7);
        romansToArab.put("VIII", 8);
        romansToArab.put("IX", 9);
        romansToArab.put("X", 10);
        romansToArab.put("XI", 11);
        romansToArab.put("XII", 12);
        romansToArab.put("XIII", 13);
        romansToArab.put("XIV", 14);
        romansToArab.put("XV", 15);
    }

    private final HattrickApi hattrickApi;

    public final Integer season;

    public Integer getSeason() {
        return season;
    }

    @Autowired
    public Hattrick(HattrickApi hattrickApi) {
        this.hattrickApi = hattrickApi;
        season = getWorldDetails().getLeagueList().stream().filter(league -> league.getLeagueName().equals("Швеция")).map(League::getSeason).findFirst().get();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public NationalTeamDetails getNationalTeamDetails(Integer countryTeamId) {
        return hattrickApi.nationalTeamDetails().teamId(countryTeamId).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public LeagueDetails getLeagueUnitByName(Integer leagueId, String leagueName) {
        Search search = hattrickApi.search().searchType(3).searchLeagueId(leagueId).searchString(leagueName).execute();
        return hattrickApi.leagueDetails().leagueLevelUnitId(search.getSearchResults().get(0).getResultId()).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public LeagueFixtures leagueUnitFixturesById(Long id, Integer season) {
        return hattrickApi.leagueFixtures().leagueLevelUnitId(id).season(season).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public Search searchLeagueUnits(Integer leagueId, String searchString, Integer page) {
        return hattrickApi.search().searchType(3).searchLeagueId(leagueId).pageIndex(page).searchString(searchString).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public List<Long> getLeagueUnitIdsForLevel(int leagueId, int level) {
        List<Long> result = new ArrayList<>();

        Search leagueSearch = hattrickApi.search().searchLeagueId(leagueId).searchType(3).searchString(arabToRomans.get(level) + ".").execute();
        leagueSearch.getSearchResults().stream().map(Result::getResultId).forEach(result::add);

        for (int page = 1; page < leagueSearch.getPages(); page++) {
            Search leagueSearchPage = hattrickApi.search().searchLeagueId(leagueId).pageIndex(page).searchType(3).searchString(arabToRomans.get(level) + ".").execute();

            leagueSearchPage.getSearchResults().stream().map(Result::getResultId).forEach(result::add);
        }
        return result;
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public TeamDetails teamDetails(Long teamId) {
        return hattrickApi.teamDetails().teamID(teamId).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public LeagueDetails getLeagueUnitById(long id) {
        return hattrickApi.leagueDetails().leagueLevelUnitId(id).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public MatchesArchive getArchiveMatches(Long teamId, Integer season) {
        return hattrickApi.matchesArchive().season(season).teamId(teamId).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public MatchesArchive getCurrentSeasonMatches(Long teamId) {
        return hattrickApi.matchesArchive().teamId(teamId).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public MatchLineUp getMatchLineUp(Long matchId, Long teamId) {
        return hattrickApi.matchLineUp().matchId(matchId).teamId(teamId).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public MatchDetails getMatchDetails(Long matchId) {
        return hattrickApi.matchDetails().matchId(matchId).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public WorldDetails getWorldDetails() {
        return hattrickApi.worldDetails().execute();
    }

    public Matches getLatestTeamMatches(Long teamId) {
        return hattrickApi.matches().teamId(teamId.intValue()).lastMatchDate(new Date()).execute();
    }

    @Retryable(value = {HattrickChppException.class, HattrickTransferException.class}, maxAttempts = 10, backoff = @Backoff(delay = 15000L))
    public Players getPlayersFromTeam(Long teamId) {
        return hattrickApi.players().teamID(teamId).includeMatchInfo(true).execute();
    }
}
