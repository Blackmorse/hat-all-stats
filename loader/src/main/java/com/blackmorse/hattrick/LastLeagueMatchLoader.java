package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.matches.model.Match;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.model.LeagueInfoWithLeagueUnitId;
import com.blackmorse.hattrick.model.TeamLeague;
import com.blackmorse.hattrick.model.TeamLeagueMatch;
import com.blackmorse.hattrick.model.enums.MatchType;
import com.blackmorse.hattrick.subscribers.MatchDetailsSubscriber;
import com.blackmorse.hattrick.utils.Utils;
import com.google.common.collect.Lists;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
public class LastLeagueMatchLoader {
    private static final int CHUNK_SIZE = 100;

    private final Scheduler scheduler;
    private final Hattrick hattrick;

    private final MatchDetailsSubscriber matchDetailsSubscriber;
    private final HattrickService hattrickService;

    @Autowired
    public LastLeagueMatchLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                         Hattrick hattrick,
                         MatchDetailsSubscriber matchDetailsSubscriber, HattrickService hattrickService) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
        this.matchDetailsSubscriber = matchDetailsSubscriber;
        this.hattrickService = hattrickService;
    }

    public void load(List<String> countryNames) {
        List<LeagueInfoWithLeagueUnitId> leagueUnits = hattrickService.getLeagueUnitsIdsForCountries(countryNames);

        List<List<LeagueInfoWithLeagueUnitId>> leagueUnitsChunks = Lists.partition(leagueUnits, CHUNK_SIZE);

        for (List<LeagueInfoWithLeagueUnitId> leagueUnitsChunk : leagueUnitsChunks) {

            List<TeamLeague> teams = hattrickService.getNonBotTeamsFromLeagueUnitIds(leagueUnitsChunk);

            List<MatchDetails> matchDetails = Flowable.fromIterable(teams)
                    .parallel().runOn(scheduler)
                    .map(this::getLastLeagueMatch)
                    .map(hattrickService::matchDetailsFromMatch)
                    .sequential()
                    .toList()
                    .blockingGet();

            matchDetails.forEach(matchDetailsSubscriber::onNext);
            matchDetailsSubscriber.onComplete();
        }
    }

    public TeamLeagueMatch getLastLeagueMatch(TeamLeague teamLeague) {
        Match match = hattrick.getLatestTeamMatches(teamLeague.getTeamId())
                .getTeam()
                .getMatchList().stream()
                .filter(m -> m.getMatchType().equals(MatchType.LEAGUE_MATCH))
                .sorted(Comparator.comparing(Match::getMatchDate).reversed())
                .findFirst().get();

        return TeamLeagueMatch.builder()
                .matchId(match.getMatchId())
                .date(match.getMatchDate())
                .matchRound(Utils.roundNumber(match.getMatchDate(), teamLeague.getNextRoundDate(), teamLeague.getNextMatchRound()))
                .teamLeague(teamLeague)
                .season(teamLeague.getCurrentSeason())
                .build();
    }
}
