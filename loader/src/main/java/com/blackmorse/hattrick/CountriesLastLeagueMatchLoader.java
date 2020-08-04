package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.AlltidLike;
import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.teamdetails.model.Team;
import com.blackmorse.hattrick.api.teamdetails.model.Trophy;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.clickhouse.PlayersJoiner;
import com.blackmorse.hattrick.clickhouse.TeamRankCalculator;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerEvents;
import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;
import com.blackmorse.hattrick.clickhouse.model.TeamDetails;
import com.blackmorse.hattrick.model.LeagueUnit;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.converters.MatchDetailsConverter;
import com.blackmorse.hattrick.model.converters.PlayerEventsConverter;
import com.blackmorse.hattrick.model.converters.PlayerInfoConverter;
import com.blackmorse.hattrick.model.converters.TeamDetailsConverter;
import com.blackmorse.hattrick.model.enums.TrophyTypeId;
import com.blackmorse.hattrick.telegram.Telegram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final Hattrick hattrick;
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
                                          PromotionsLoader promotionsLoader, Hattrick hattrick) {
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
        this.hattrick = hattrick;
    }

    public void load(List<String> countryNames) {

        log.info("Starting to load countries: {}", countryNames);

        for (String countryName : countryNames) {
            boolean writtenToClickhouse  = false;
            try {
                League league = hattrickService.getLeagueByCountryName(countryName);

                log.info("Loading country {}, leagueId: {}...", countryName, league.getLeagueId());
                log.info("There is {} active teams in ({}, {})", league.getActiveTeams(), countryName, league.getLeagueId());
                List<LeagueUnit> allLeagueUnitIdsForCountry = hattrickService.getAllLeagueUnitIdsForCountry(countryName);

                log.info("There are {} league units in ({}, {})", allLeagueUnitIdsForCountry.size(), countryName, league.getLeagueId());
                List<TeamWithMatchDetails> lastTeamWithMatchDetails = hattrickService.getLastMatchDetails(allLeagueUnitIdsForCountry);

                AtomicInteger newCounter = new AtomicInteger();

                lastTeamWithMatchDetails.stream().parallel().forEach(lastTeamWithMatchDetail -> {
                    Long team = lastTeamWithMatchDetail.getTeamWithMatch().getTeam().getId();

                    com.blackmorse.hattrick.api.teamdetails.model.TeamDetails teamDetails = hattrick.getTeamDetails(team);

                    Team team2 = teamDetails.getTeams().stream().filter(team1 -> team1.getTeamId() != null && team1.getTeamId().equals(team))
                            .findAny().get();
                    newCounter.incrementAndGet();
                    log.info("new counter: {}", newCounter.get());
                    if (team2.getTrophyList().stream().map(Trophy::getTrophyTypeId).collect(Collectors.toList()).contains(TrophyTypeId.UNKNOWN)) {
                        for (int i = 0; i <100 ; i++) {
                            log.info("!!!!!!!!!!!!!");
                            log.info("{}", team2.getTeamId());
                        }
                        System.exit(0);
                    }
                });

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
