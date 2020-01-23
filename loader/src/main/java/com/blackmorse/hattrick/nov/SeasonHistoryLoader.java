package com.blackmorse.hattrick.nov;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.clickhouse.ClickhouseBatcher;
import com.blackmorse.hattrick.clickhouse.ClickhouseBatcherFactory;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.nov.SeasonHistoryLoader.Match;
import com.google.common.collect.Lists;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SeasonHistoryLoader {
    private final Scheduler scheduler;
    private final Hattrick hattrick;
    private final NewHattrickService newHattrickService;
    private final ClickhouseBatcher<MatchDetails> matchDetailsBatcher;

    @Autowired
    public SeasonHistoryLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                               Hattrick hattrick,
                               NewHattrickService newHattrickService,
                               ClickhouseBatcherFactory clickhouseBatcherFactory) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
        this.newHattrickService = newHattrickService;
        matchDetailsBatcher = clickhouseBatcherFactory.createMatchDetails();
    }

    public void load(List<String> countryNames, Integer season) {

        List<LeagueUnitId> allLeagueUnitIdsForCountry = newHattrickService.getAllLeagueUnitIdsForCountry(countryNames);

        List<List<LeagueUnitId>> allLeagueUnitIdsForCountryChunks = Lists.partition(allLeagueUnitIdsForCountry, 150);


        for (List<LeagueUnitId> allLeagueUnitIdsForCountryChunk : allLeagueUnitIdsForCountryChunks) {
            List<TeamWithMatches> allTeamsWithMatches = newHattrickService.getAllTeamsWithMatches(allLeagueUnitIdsForCountryChunk, season);

            List<MatchDetails> matchDetailsBatch = newHattrickService.getMatchDetails(allTeamsWithMatches);

            matchDetailsBatcher.writeToClickhouse(matchDetailsBatch);
        }
    }


    @Data
    @Builder
    public static class League {
        private final Integer id;
        private final Integer seasonOffset;
        private final Integer nextRound;
        private final Integer maxLevel;
    }

    @Data
    @Builder
    public static class LeagueUnitId {
        private final League league;
        private final Long id;
    }

    @Data
    @Builder
    public static class LeagueUnit {
        private final League league;

        private final Long id;
        private final String name;
        private final Integer level;
    }

    @Data
    @Builder
    public static class Team {
        private final LeagueUnit leagueUnit;

        private final Long id;
        private final String name;
    }

    @Data
    @Builder
    public static class TeamWithMatches {
        private final Team team;
        private final List<Match> matches;
    }

    @Data
    @Builder
    public static class TeamWithMatch {
        private final Team team;
        private final Match match;
    }

    @Data
    @Builder
    public static class Match {
        private Long id;
        private Integer round;
        private Date date;
        private Integer season;
    }
}
