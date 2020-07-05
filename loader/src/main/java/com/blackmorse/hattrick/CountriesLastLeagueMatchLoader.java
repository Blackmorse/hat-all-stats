package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.AlltidLike;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.clickhouse.PlayersJoiner;
import com.blackmorse.hattrick.clickhouse.TeamRankCalculator;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerEvents;
import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;
import com.blackmorse.hattrick.model.LeagueUnit;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.converters.MatchDetailsConverter;
import com.blackmorse.hattrick.model.converters.PlayerEventsConverter;
import com.blackmorse.hattrick.model.converters.PlayerInfoConverter;
import com.blackmorse.hattrick.telegram.Telegram;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
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
    private final TeamRankCalculator teamRankCalculator;
    private final Telegram telegram;
    private final AlltidLike alltidLike;
    private Runnable callback;

    @Autowired
    public CountriesLastLeagueMatchLoader(HattrickService hattrickService,
                                          @Qualifier("matchDetailsWriter") ClickhouseWriter<MatchDetails> matchDetailsWriter,
                                          @Qualifier("playerEventsWriter") ClickhouseWriter<PlayerEvents> playerEventsWriter,
                                          @Qualifier("playerInfoWriter") ClickhouseWriter<PlayerInfo> playerInfoWriter,
                                          PlayersJoiner playersJoiner,
                                          MatchDetailsConverter matchDetailsConverter,
                                          PlayerEventsConverter playerEventsConverter,
                                          PlayerInfoConverter playerInfoConverter,
                                          TeamRankCalculator teamRankCalculator,
                                          AlltidLike alltidLike,
                                          Telegram telegram) {
        this.hattrickService = hattrickService;
        this.matchDetailsWriter = matchDetailsWriter;
        this.playerEventsWriter = playerEventsWriter;
        this.playerInfoWriter = playerInfoWriter;
        this.playersJoiner = playersJoiner;
        this.matchDetailsConverter = matchDetailsConverter;
        this.playerEventsConverter = playerEventsConverter;
        this.playerInfoConverter = playerInfoConverter;
        this.teamRankCalculator = teamRankCalculator;
        this.alltidLike = alltidLike;
        this.telegram = telegram;
    }

    public void load(List<String> countryNames) {

        log.info("Starting to load countries: {}", countryNames);

        for (String countryName : countryNames) {
            try {
                League league = hattrickService.getLeagueByCountryName(countryName);

                log.info("Loading country {}, leagueId: {}...", countryName, league.getLeagueId());
                log.info("There is {} active teams in ({}, {})", league.getActiveTeams(), countryName, league.getLeagueId());
                List<LeagueUnit> allLeagueUnitIdsForCountry = hattrickService.getAllLeagueUnitIdsForCountry(Arrays.asList(countryName));

                List<List<LeagueUnit>> allLeagueUnitIdsForCountryChunks = Lists.partition(allLeagueUnitIdsForCountry, 350);

                for (List<LeagueUnit> allLeagueUnitIdsForCountryChunk : allLeagueUnitIdsForCountryChunks) {
                    log.info("Chunk of leagueUnits size {} for ({}, {})", allLeagueUnitIdsForCountryChunk.size(), countryName, league.getLeagueId());
                    List<TeamWithMatchDetails> lastTeamWithMatchDetails = hattrickService.getLastMatchDetails(allLeagueUnitIdsForCountryChunk);

                    List<MatchDetails> lastMatchDetails = lastTeamWithMatchDetails.stream().map(matchDetailsConverter::convert).collect(Collectors.toList());

                    List<PlayerEvents> playerEvents = lastTeamWithMatchDetails.stream()
                            .flatMap(playerEventsConverter::convert)
                            .collect(Collectors.toList());

                    List<PlayerInfo> playerInfos = hattrickService.getPlayersFromTeam(lastTeamWithMatchDetails)
                            .stream()
                            .flatMap(playerInfoConverter::convert)
                            .collect(Collectors.toList());

                    log.info("Writing match details for ({}, {}) to Clickhouse: {} rows", countryName, league.getLeagueId(), lastMatchDetails.size());
                    matchDetailsWriter.writeToClickhouse(lastMatchDetails);
                    log.info("Writing player events for ({}, {}) to Clickhouse: {} rows", countryName, league.getLeagueId(), playerEvents.size());
                    playerEventsWriter.writeToClickhouse(playerEvents);
                    log.info("Writing player info for ({}, {}) to Clickhouse: {} rows", countryName, league.getLeagueId(), playerInfos.size());
                    playerInfoWriter.writeToClickhouse(playerInfos);
                }
                log.info("Joining player_stats for ({}, {}) ", countryName, league.getLeagueId());
                playersJoiner.join(league);
                log.info("Calculating team ranks for ({}, {})", countryName, league.getLeagueId());
                teamRankCalculator.calculate(league);
                log.info("Send request to web about new round...");
                alltidLike.updateRoundInfo(league.getSeason() - league.getSeasonOffset(), league.getLeagueId(), league.getMatchRound() - 1);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                telegram.send(e.getMessage());
            } finally {
                if (callback != null) {
                    callback.run();
                }
            }
        }
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
}
