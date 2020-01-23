package com.blackmorse.hattrick.nov;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.leaguefixtures.model.LeagueFixtures;
import com.blackmorse.hattrick.api.leaguefixtures.model.Match;
import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.api.search.model.Result;
import com.blackmorse.hattrick.api.teamdetails.model.TeamDetails;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class NewHattrickService {
    private final Scheduler scheduler;
    private final Hattrick hattrick;
    private final AtomicLong teamsCounter  = new AtomicLong();
    private final AtomicLong matchDetailsCounter  = new AtomicLong();

    public NewHattrickService(@Qualifier("apiExecutor") ExecutorService executorService,
                              Hattrick hattrick) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
    }

    public List<SeasonHistoryLoader.LeagueUnitId> getAllLeagueUnitIdsForCountry(List<String> countryNames) {
        Flowable<SeasonHistoryLoader.LeagueUnitId> leagueIds = Flowable.fromIterable(
                hattrick.getWorldDetails().getLeagueList()
                        .stream()
                        .filter(league -> countryNames.contains(league.getLeagueName())).collect(Collectors.toList())
        ).map(league -> SeasonHistoryLoader.League.builder()
                .id(league.getLeagueId())
                .nextRound(league.getMatchRound())
                .seasonOffset(league.getSeasonOffset())
                .maxLevel(league.getNumberOfLevels())
                .build()
        ).map(league -> {
            LeagueDetails leagueUnit = hattrick.getLeagueUnitByName(league.getId(), "II.1");
            return SeasonHistoryLoader.LeagueUnit.builder()
                    .league(league)
                    .id(leagueUnit.getLeagueLevelUnitId())
                    .name(leagueUnit.getLeagueLevelUnitName())
                    .build();
        }).flatMap(this::getAllLeagueIds);

        return leagueIds.toList().blockingGet();
    }

    private Flowable<SeasonHistoryLoader.LeagueUnitId> getAllLeagueIds(SeasonHistoryLoader.LeagueUnit twoOne) {

        return Flowable.just(new SeasonHistoryLoader.LeagueUnitId(twoOne.getLeague(), twoOne.getId() - 1)).concatWith(
                Flowable.fromIterable(IntStream.range(2, twoOne.getLeague().getMaxLevel() + 1)
                        .boxed()
                        .collect(Collectors.toList()))
                        .map(level -> hattrick.searchLeagueUnits(twoOne.getLeague().getId(), Hattrick.arabToRomans.get(level) + ".", 0))
                        .flatMap(searchResult ->
                                Flowable.fromIterable(
                                        IntStream.range(0, searchResult.getPages())
                                                .mapToObj(page -> new StringAndNumber(searchResult.getSearchParams().getSearchString(), page))
                                                .collect(Collectors.toList())
                                )
                        )
                        .parallel()
                        .runOn(scheduler)
                        .map(leagueLevelWPage ->
                                hattrick.searchLeagueUnits(twoOne.getLeague().getId(), leagueLevelWPage.getString(), leagueLevelWPage.getNumber())
                        ).flatMap(search -> Flowable.fromIterable(
                        search.getSearchResults().stream().map(Result::getResultId).collect(Collectors.toList())
                ))
                        .map(number -> new SeasonHistoryLoader.LeagueUnitId(twoOne.getLeague(), number)).sequential());
    }

    public List<SeasonHistoryLoader.TeamWithMatches> getAllTeamsWithMatches(List<SeasonHistoryLoader.LeagueUnitId> leagueUnitIds, Integer season) {
        return Flowable.fromIterable(leagueUnitIds)
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueUnitId -> {
                    Integer offset = leagueUnitId.getLeague().getSeasonOffset();
                    Integer offsettedSeason = season + offset;
                    LeagueFixtures leagueFixtures = hattrick.leagueUnitFixturesById(leagueUnitId.getId(), offsettedSeason);

                    SeasonHistoryLoader.LeagueUnit leagueUnit = //new SeasonHistoryLoader.LeagueUnit(leagueUnitId.getLeague(), leagueFixtures.getLeagueLevelUnitId(), leagueFixtures.getLeagueLevelUnitName());
                            SeasonHistoryLoader.LeagueUnit.builder()
                                    .league(leagueUnitId.getLeague())
                                    .id(leagueFixtures.getLeagueLevelUnitId())
                                    .name(leagueFixtures.getLeagueLevelUnitName())
                                    .level(leagueLevelFromName(leagueFixtures.getLeagueLevelUnitName()))
                                    .build();

                    Map<SeasonHistoryLoader.Team, List<SeasonHistoryLoader.Match>> teamMatchMap = new HashMap<>();

                    for (Match match : leagueFixtures.getMatches()) {
                        if (season.equals(hattrick.getSeason()) && leagueUnit.getLeague().getNextRound() <= match.getMatchRound()) continue;

                        SeasonHistoryLoader.Team homeTeam = new SeasonHistoryLoader.Team(leagueUnit, match.getHomeTeam().getHomeTeamId(), match.getHomeTeam().getHomeTeamName());
                        teamMatchMap.computeIfAbsent(homeTeam, team -> new ArrayList<>())//.add(new SeasonHistoryLoader.Match(match.getMatchId(), match.getMatchRound(), match.getMatchDate()));
                            .add(SeasonHistoryLoader.Match.builder()
                                    .id(match.getMatchId())
                                    .round(match.getMatchRound())
                                    .date(match.getMatchDate())
                                    .season(season)
                                    .build());
                        SeasonHistoryLoader.Team awayTeam = new SeasonHistoryLoader.Team(leagueUnit, match.getAwayTeam().getAwayTeamId(), match.getAwayTeam().getAwayTeamName());
                        teamMatchMap.computeIfAbsent(awayTeam, team -> new ArrayList<>())//.add(SeasonHistoryLoader.Match(match.getMatchId(), match.getMatchRound(), match.getMatchDate()));
                                .add(SeasonHistoryLoader.Match.builder()
                                        .id(match.getMatchId())
                                        .round(match.getMatchRound())
                                        .date(match.getMatchDate())
                                        .season(season)
                                        .build());
                    }

                    return Flowable.fromIterable(teamMatchMap.entrySet().stream()
                            .map(entry -> new SeasonHistoryLoader.TeamWithMatches(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList()));
                }).filter(teamWithMatches -> {

                    TeamDetails teamDetails = hattrick.teamDetails(teamWithMatches.getTeam().getId());

                    log.info("{} teamDetails {} is OK: {}", teamsCounter.incrementAndGet(), teamWithMatches.getTeam().getLeagueUnit().getName(), teamDetails.getUser().getUserId() != 0L);
                    return teamDetails.getUser().getUserId() != 0L;
                })
                .sequential()
                .toList()
                .blockingGet();
    }

    @Data
    @Builder
    private static class StringAndNumber {
        private final String string;
        private final Integer number;
    }

    private static Integer leagueLevelFromName(String name) {
        if(!name.contains(",")) return 1;
        else {
            return Hattrick.romansToArab.get(name.split("\\.")[1]);
        }
    }

    public List<MatchDetails> getMatchDetails(List<SeasonHistoryLoader.TeamWithMatches> teamWMatches) {
        return Flowable.fromIterable(teamWMatches)
                .flatMap(teamWithMatches -> Flowable.fromIterable(
                        teamWithMatches.getMatches().stream()
                                .map(match -> new SeasonHistoryLoader.TeamWithMatch(teamWithMatches.getTeam(), match))
                                .collect(Collectors.toList())
                ))
                .parallel()
                .runOn(scheduler)
                .map(teamWithMatch -> {
                    com.blackmorse.hattrick.api.matchdetails.model.MatchDetails matchDetails = hattrick.getMatchDetails(teamWithMatch.getMatch().getId());

                    Long homeTeamId = matchDetails.getMatch().getHomeTeam().getHomeTeamId();

                    HomeAwayTeam homeAwayTeam;
                    if (homeTeamId.equals(teamWithMatch.getTeam().getId())) {
                        homeAwayTeam = matchDetails.getMatch().getHomeTeam();
                    } else {
                        homeAwayTeam = matchDetails.getMatch().getAwayTeam();
                    }

                    log.info("Match {}", matchDetailsCounter.incrementAndGet());

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
                })
                .sequential()
                .toList()
                .blockingGet();
    }
}