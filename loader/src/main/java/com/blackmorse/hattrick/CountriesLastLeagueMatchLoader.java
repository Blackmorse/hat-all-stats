package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.AlltidLike;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.clickhouse.PlayersJoiner;
import com.blackmorse.hattrick.clickhouse.TeamRankCalculator;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerEvents;
import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;
import com.blackmorse.hattrick.clickhouse.model.TeamDetails;
import com.blackmorse.hattrick.model.LeagueUnit;
import com.blackmorse.hattrick.model.TeamWithMatchAndTeamDetails;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.converters.MatchDetailsConverter;
import com.blackmorse.hattrick.model.converters.PlayerEventsConverter;
import com.blackmorse.hattrick.model.converters.PlayerInfoConverter;
import com.blackmorse.hattrick.model.converters.TeamDetailsConverter;
import com.blackmorse.hattrick.telegram.Telegram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CountriesLastLeagueMatchLoader {
    private final HattrickService hattrickService;
    private final ClickhouseWriter<MatchDetails> matchDetailsWriter;
    private final ClickhouseWriter<PlayerEvents> playerEventsWriter;
    private final ClickhouseWriter<PlayerInfo> playerInfoWriter;
    private final ClickhouseWriter<TeamDetails> teamDetailsWriter;
    private final PlayersJoiner playersJoiner;
    private final MatchDetailsConverter matchDetailsConverter;
    private final PlayerEventsConverter playerEventsConverter;
    private final PlayerInfoConverter playerInfoConverter;
    private final TeamDetailsConverter teamDetailsConverter;
    private final TeamRankCalculator teamRankCalculator;
    private final Telegram telegram;
    private final AlltidLike alltidLike;
    private final PromotionsLoader promotionsLoader;
    private Runnable callback;

    @Autowired
    public CountriesLastLeagueMatchLoader(HattrickService hattrickService,
                                          @Qualifier("matchDetailsWriter") ClickhouseWriter<MatchDetails> matchDetailsWriter,
                                          @Qualifier("playerEventsWriter") ClickhouseWriter<PlayerEvents> playerEventsWriter,
                                          @Qualifier("playerInfoWriter") ClickhouseWriter<PlayerInfo> playerInfoWriter,
                                          ClickhouseWriter<TeamDetails> teamDetailsWriter,
                                          PlayersJoiner playersJoiner,
                                          MatchDetailsConverter matchDetailsConverter,
                                          PlayerEventsConverter playerEventsConverter,
                                          PlayerInfoConverter playerInfoConverter,
                                          TeamDetailsConverter teamDetailsConverter,
                                          TeamRankCalculator teamRankCalculator,
                                          AlltidLike alltidLike,
                                          Telegram telegram,
                                          PromotionsLoader promotionsLoader) {
        this.hattrickService = hattrickService;
        this.matchDetailsWriter = matchDetailsWriter;
        this.playerEventsWriter = playerEventsWriter;
        this.playerInfoWriter = playerInfoWriter;
        this.teamDetailsWriter = teamDetailsWriter;
        this.playersJoiner = playersJoiner;
        this.matchDetailsConverter = matchDetailsConverter;
        this.playerEventsConverter = playerEventsConverter;
        this.playerInfoConverter = playerInfoConverter;
        this.teamDetailsConverter = teamDetailsConverter;
        this.teamRankCalculator = teamRankCalculator;
        this.alltidLike = alltidLike;
        this.telegram = telegram;
        this.promotionsLoader = promotionsLoader;
    }

    public void load(List<String> countryNames) {

        log.info("Starting to load countries: {}", countryNames);

        for (String countryName : countryNames) {
            boolean writtenToClickhouse  = false;
            try {
                League league = hattrickService.getLeagueByCountryName(countryName);

                log.info("Loading country {}, leagueId: {} with {} active teams...", countryName, league.getLeagueId(), league.getActiveTeams());
                List<LeagueUnit> allLeagueUnitIdsForCountry = hattrickService.getAllLeagueUnitIdsForCountry(countryName);

                log.info("There are {} league units in ({}, {})", allLeagueUnitIdsForCountry.size(), countryName, league.getLeagueId());
                List<TeamWithMatchDetails> lastTeamWithMatchDetails = hattrickService.getLastMatchDetails(allLeagueUnitIdsForCountry);

                List<MatchDetails> lastMatchDetails = lastTeamWithMatchDetails.stream()
                        .map(matchDetailsConverter::convert)
                        .collect(Collectors.toList());

                List<PlayerEvents> playerEvents = lastTeamWithMatchDetails.stream()
                        .flatMap(playerEventsConverter::convert)
                        .collect(Collectors.toList());

                log.info("Loaded {} Match Details", lastTeamWithMatchDetails.size());

                List<PlayerInfo> playerInfos = hattrickService.getPlayersFromTeam(lastTeamWithMatchDetails)
                        .stream()
                        .flatMap(playerInfoConverter::convert)
                        .collect(Collectors.toList());

                log.info("Loaded {} player infos", playerInfos.size());

                List<TeamDetails> teamDetails = hattrickService.getTeamDetails(lastTeamWithMatchDetails)
                        .stream()
                        .map(teamDetailsConverter::convert)
                        .collect(Collectors.toList());

                log.info("Loaded {} Team Details", teamDetails.size());

                writtenToClickhouse = true;
                log.info("Writing match details to Clickhouse: {} rows", lastMatchDetails.size());
                matchDetailsWriter.writeToClickhouse(lastMatchDetails);
                log.info("Writing player events to Clickhouse: {} rows", playerEvents.size());
                playerEventsWriter.writeToClickhouse(playerEvents);
                log.info("Writing player info  to Clickhouse: {} rows", playerInfos.size());
                playerInfoWriter.writeToClickhouse(playerInfos);

                log.info("Writing teams details to Clickhouse: {} rows", teamDetails.size());
                teamDetailsWriter.writeToClickhouse(teamDetails);

                log.info("Joining player_stats for ({}, {}) ", countryName, league.getLeagueId());
                playersJoiner.join(league);
                log.info("Calculating team ranks for ({}, {})", countryName, league.getLeagueId());
                teamRankCalculator.calculate(league);
                log.info("Send request to web about new round...");
                alltidLike.updateRoundInfo(league.getSeason() - league.getSeasonOffset(), league.getLeagueId(), league.getMatchRound() - 1);
                log.info("Request successfully sent");
                //Load promotions
                if (league.getMatchRound() - 1 == 14) {
                    log.info("It's last round of season. Time to load promotions!");
                    promotionsLoader.load(countryName, allLeagueUnitIdsForCountry);
                }
                if (callback != null) {
                    callback.run();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                telegram.send(e.getMessage());
                if(!writtenToClickhouse) {
                    throw new RuntimeException(e);
                }
                log.error("Seems that some data for {} was written to CH and then failed. You must take a look", countryName);
            }
        }
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
}
