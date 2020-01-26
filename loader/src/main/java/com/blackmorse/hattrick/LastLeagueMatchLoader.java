package com.blackmorse.hattrick;

import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.model.LeagueUnitId;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.converters.MatchDetailsConverter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LastLeagueMatchLoader {
    private final HattrickService hattrickService;
    private final ClickhouseWriter<MatchDetails> matchDetailsWriter;
    private final MatchDetailsConverter matchDetailsConverter;

    @Autowired
    public LastLeagueMatchLoader(HattrickService hattrickService,
                                 ClickhouseWriter<MatchDetails> matchDetailsWriter,
                                 MatchDetailsConverter matchDetailsConverter) {
        this.hattrickService = hattrickService;
        this.matchDetailsWriter = matchDetailsWriter;
        this.matchDetailsConverter = matchDetailsConverter;
    }

    public void load(List<String> countryNames) {
        List<LeagueUnitId> allLeagueUnitIdsForCountry = hattrickService.getAllLeagueUnitIdsForCountry(countryNames);

        List<List<LeagueUnitId>> allLeagueUnitIdsForCountryChunks = Lists.partition(allLeagueUnitIdsForCountry, 300);

        for (List<LeagueUnitId> allLeagueUnitIdsForCountryChunk : allLeagueUnitIdsForCountryChunks) {
            List<TeamWithMatchDetails> lastTeamWithMatchDetails = hattrickService.getLastMatchDetails(allLeagueUnitIdsForCountryChunk);

            List<MatchDetails> lastMatchDetails = lastTeamWithMatchDetails.stream().map(matchDetailsConverter::convert).collect(Collectors.toList());

            matchDetailsWriter.writeToClickhouse(lastMatchDetails);
        }
    }
}
