package com.blackmorse.hattrick;

import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.model.LeagueUnitId;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.TeamWithMatches;
import com.blackmorse.hattrick.model.converters.MatchDetailsConverter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SeasonHistoryLoader {
    private final HattrickService hattrickService;
    private final ClickhouseWriter<MatchDetails> matchDetailsBatcher;
    private final MatchDetailsConverter matchDetailsConverter;

    @Autowired
    public SeasonHistoryLoader(HattrickService hattrickService,
                               @Qualifier("matchDetailsWriter")ClickhouseWriter<MatchDetails> matchDetailsWriter,
                               MatchDetailsConverter matchDetailsConverter) {
        this.hattrickService = hattrickService;
        this.matchDetailsBatcher = matchDetailsWriter;
        this.matchDetailsConverter = matchDetailsConverter;
    }

    public void load(List<String> countryNames, Integer season) {

        List<LeagueUnitId> allLeagueUnitIdsForCountry = hattrickService.getAllLeagueUnitIdsForCountry(countryNames);

        List<List<LeagueUnitId>> allLeagueUnitIdsForCountryChunks = Lists.partition(allLeagueUnitIdsForCountry, 150);


        for (List<LeagueUnitId> allLeagueUnitIdsForCountryChunk : allLeagueUnitIdsForCountryChunks) {
            List<TeamWithMatches> allTeamsWithMatches = hattrickService.getAllTeamsWithMatches(allLeagueUnitIdsForCountryChunk, season);

            List<TeamWithMatchDetails> teamWithMatchDetailsBatch = hattrickService.getMatchDetails(allTeamsWithMatches);

            List<MatchDetails> matchDetailsBatch = teamWithMatchDetailsBatch.stream().map(matchDetailsConverter::convert).collect(Collectors.toList());
            matchDetailsBatcher.writeToClickhouse(matchDetailsBatch);
        }
    }
}
