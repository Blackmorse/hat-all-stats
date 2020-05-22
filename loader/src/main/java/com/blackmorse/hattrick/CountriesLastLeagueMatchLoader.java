package com.blackmorse.hattrick;

import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.clickhouse.PlayersJoiner;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerEvents;
import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;
import com.blackmorse.hattrick.model.LeagueUnitId;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.converters.MatchDetailsConverter;
import com.blackmorse.hattrick.model.converters.PlayerEventsConverter;
import com.blackmorse.hattrick.model.converters.PlayerInfoConverter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CountriesLastLeagueMatchLoader {
    private final HattrickService hattrickService;
    private final ClickhouseWriter<MatchDetails> matchDetailsWriter;
    private final ClickhouseWriter<PlayerEvents> playerEventsWriter;
    private final ClickhouseWriter<PlayerInfo> playerInfoWriter;
    private final PlayersJoiner playersJoiner;
    private final MatchDetailsConverter matchDetailsConverter;
    private final PlayerEventsConverter playerEventsConverter;
    private final PlayerInfoConverter playerInfoConverter;
    private Runnable callback;

    @Autowired
    public CountriesLastLeagueMatchLoader(HattrickService hattrickService,
                                          @Qualifier("matchDetailsWriter") ClickhouseWriter<MatchDetails> matchDetailsWriter,
                                          @Qualifier("playerEventsWriter") ClickhouseWriter<PlayerEvents> playerEventsWriter,
                                          @Qualifier("playerInfoWriter") ClickhouseWriter<PlayerInfo> playerInfoWriter,
                                          PlayersJoiner playersJoiner,
                                          MatchDetailsConverter matchDetailsConverter,
                                          PlayerEventsConverter playerEventsConverter,
                                          PlayerInfoConverter playerInfoConverter) {
        this.hattrickService = hattrickService;
        this.matchDetailsWriter = matchDetailsWriter;
        this.playerEventsWriter = playerEventsWriter;
        this.playerInfoWriter = playerInfoWriter;
        this.playersJoiner = playersJoiner;
        this.matchDetailsConverter = matchDetailsConverter;
        this.playerEventsConverter = playerEventsConverter;
        this.playerInfoConverter = playerInfoConverter;
    }

    public void load(List<String> countryNames) {

        log.info("Starting to load countries: {}", countryNames);

        for (String countryName : countryNames) {
            try {
                List<LeagueUnitId> allLeagueUnitIdsForCountry = hattrickService.getAllLeagueUnitIdsForCountry(Arrays.asList(countryName));

                List<List<LeagueUnitId>> allLeagueUnitIdsForCountryChunks = Lists.partition(allLeagueUnitIdsForCountry, 350);

                for (List<LeagueUnitId> allLeagueUnitIdsForCountryChunk : allLeagueUnitIdsForCountryChunks) {
                    List<TeamWithMatchDetails> lastTeamWithMatchDetails = hattrickService.getLastMatchDetails(allLeagueUnitIdsForCountryChunk);

                    List<MatchDetails> lastMatchDetails = lastTeamWithMatchDetails.stream().map(matchDetailsConverter::convert).collect(Collectors.toList());

                    List<PlayerEvents> playerEvents = lastTeamWithMatchDetails.stream()
                            .flatMap(playerEventsConverter::convert)
                            .collect(Collectors.toList());

                    List<PlayerInfo> playerInfos = hattrickService.getPlayersFromTeam(lastTeamWithMatchDetails)
                            .stream()
                            .flatMap(playerInfoConverter::convert)
                            .collect(Collectors.toList());

                    matchDetailsWriter.writeToClickhouse(lastMatchDetails);
                    playerEventsWriter.writeToClickhouse(playerEvents);
                    playerInfoWriter.writeToClickhouse(playerInfos);

                    if (!lastMatchDetails.isEmpty()) {
                        MatchDetails matchDetails = lastMatchDetails.get(0);
                        playersJoiner.join(matchDetails.getSeason(), matchDetails.getLeagueId(), matchDetails.getRound());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                if (callback != null) {
                    callback.run();
                }
            }
        }
//        hattrickService.shutDown();
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
}
