package com.blackmorse.hattrick;

import com.blackmorse.hattrick.clickhouse.ClickhouseBatcher;
import com.blackmorse.hattrick.clickhouse.ClickhouseBatcherFactory;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.model.LeagueUnitId;
import com.blackmorse.hattrick.model.TeamWithMatches;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SeasonHistoryLoader {
    private final HattrickService hattrickService;
    private final ClickhouseBatcher<MatchDetails> matchDetailsBatcher;

    @Autowired
    public SeasonHistoryLoader(HattrickService hattrickService,
                               ClickhouseBatcherFactory clickhouseBatcherFactory) {
        this.hattrickService = hattrickService;
        matchDetailsBatcher = clickhouseBatcherFactory.createMatchDetails();
    }

    public void load(List<String> countryNames, Integer season) {

        List<LeagueUnitId> allLeagueUnitIdsForCountry = hattrickService.getAllLeagueUnitIdsForCountry(countryNames);

        List<List<LeagueUnitId>> allLeagueUnitIdsForCountryChunks = Lists.partition(allLeagueUnitIdsForCountry, 150);


        for (List<LeagueUnitId> allLeagueUnitIdsForCountryChunk : allLeagueUnitIdsForCountryChunks) {
            List<TeamWithMatches> allTeamsWithMatches = hattrickService.getAllTeamsWithMatches(allLeagueUnitIdsForCountryChunk, season);

            List<MatchDetails> matchDetailsBatch = hattrickService.getMatchDetails(allTeamsWithMatches);

            matchDetailsBatcher.writeToClickhouse(matchDetailsBatch);
        }
    }
}
