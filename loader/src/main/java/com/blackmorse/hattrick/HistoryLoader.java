package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.api.matchdetails.model.HomeTeam;
import com.blackmorse.hattrick.api.matchdetails.model.Match;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.model.LeagueInfoWithLeagueUnitDetails;
import com.blackmorse.hattrick.model.LeagueInfoWithLeagueUnitId;
import com.blackmorse.hattrick.model.TeamLeague;
import com.blackmorse.hattrick.model.TeamLeagueMatch;
import com.blackmorse.hattrick.model.enums.MatchType;
import com.blackmorse.hattrick.subscribers.MatchDetailsSubscriber;
import com.google.common.collect.Lists;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HistoryLoader {
    private static final int CHUNK_SIZE = 100;

    private final Scheduler scheduler;
    private final Hattrick hattrick;

    private final MatchDetailsSubscriber matchDetailsSubscriber;
    private final HattrickService hattrickService;


    private AtomicLong matchesCount = new AtomicLong();
    private AtomicLong leagues = new AtomicLong();
    private AtomicLong matchDetailsCount = new AtomicLong();

    @Autowired
    public HistoryLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                         Hattrick hattrick,
                         MatchDetailsSubscriber matchDetailsSubscriber, HattrickService hattrickService) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
        this.matchDetailsSubscriber = matchDetailsSubscriber;
        this.hattrickService = hattrickService;
    }

    public void load(List<String> countryNames, Integer seasonNumber) {
        List<LeagueInfoWithLeagueUnitId> leagueUnits = hattrickService.getLeagueUnitsIdsForCountries(countryNames);

        List<List<LeagueInfoWithLeagueUnitId>> leagueUnitsChunks = Lists.partition(leagueUnits, CHUNK_SIZE);

        for (List<LeagueInfoWithLeagueUnitId> leagueUnitsChunk : leagueUnitsChunks) {

            List<TeamLeague> teams = hattrickService.getNonBotTeamsFromLeagueUnitIds(leagueUnitsChunk);

            List<TeamLeagueMatch> teamLeagueMatches = Flowable.fromIterable(teams)
                    .parallel()
                    .runOn(scheduler)
                    .map((TeamLeague teamLeague) -> getMatches(teamLeague, seasonNumber))
                    .flatMap(Flowable::fromIterable)
                    .sequential()
                    .toList()
                    .blockingGet();

            List<MatchDetails> matchDetails = Flowable.fromIterable(teamLeagueMatches)
                    .parallel()
                    .runOn(scheduler)
                    .map(this::matchDetailsFromMatch)
                    .sequential()
                    .toList()
                    .blockingGet();

            matchDetails.forEach(matchDetailsSubscriber::onNext);
            matchDetailsSubscriber.onComplete();
        }
    }

    private List<TeamLeagueMatch> getMatches(TeamLeague teamLeague, Integer seasonNumber) {
        return
                hattrick.getArchiveMatches(teamLeague.getTeamId(), seasonNumber + teamLeague.getSeasonOffset()).getTeam().getMatchList()
                        .stream()
                        .filter(match -> match.getMatchType().equals(MatchType.LEAGUE_MATCH))
                        .map(match -> {
                            log.info("Got {} matches", matchesCount.incrementAndGet());
                            return TeamLeagueMatch.builder()
                                    .matchId(match.getMatchId())
                                    .date(match.getMatchDate())
                                    .matchRound(roundNumber(match.getMatchDate(), teamLeague.getNextRoundDate(), teamLeague.getNextMatchRound()))
                                    .teamLeague(teamLeague)
                                    .season(seasonNumber)
                                    .build();
                        })
                        .collect(Collectors.toList()
        );
    }

    private Integer roundNumber(Date matchDate, Date lastLeagueMatchDate, Integer currentRound) {
        long daysBefore = (lastLeagueMatchDate.getTime() - matchDate.getTime()) / (1000 * 60 * 60 * 24 * 7);
        return (int)(currentRound - daysBefore);
    }

    private MatchDetails matchDetailsFromMatch(TeamLeagueMatch teamLeagueMatch) {
        com.blackmorse.hattrick.api.matchdetails.model.MatchDetails matchDetails = hattrick.getMatchDetails(teamLeagueMatch.getMatchId());
        HomeAwayTeam homeAwayTeam;
        Match match = matchDetails.getMatch();
        HomeTeam homeTeam = match.getHomeTeam();
        Long homeTeamId = homeTeam.getHomeTeamId();
        Long teamId = teamLeagueMatch.getTeamLeague().getTeamId();
        if (homeTeamId.equals(teamId)) {
            homeAwayTeam = matchDetails.getMatch().getHomeTeam();
        } else {
            homeAwayTeam = matchDetails.getMatch().getAwayTeam();
        }
        log.info("Got {} matchDetails", matchDetailsCount.incrementAndGet());
        return MatchDetails.builder()
                .season(teamLeagueMatch.getSeason())
                .leagueId(teamLeagueMatch.getTeamLeague().getLeagueId())
                .divisionLevel(teamLeagueMatch.getTeamLeague().getLeagueLevel())
                .leagueUnitId(teamLeagueMatch.getTeamLeague().getLeagueLevelUnitId())
                .leagueUnitName(teamLeagueMatch.getTeamLeague().getLeagueUnitName())
                .teamId(teamLeagueMatch.getTeamLeague().getTeamId())
                .teamName(teamLeagueMatch.getTeamLeague().getTeamName())
                .date(matchDetails.getMatch().getMatchDate())
                .round(teamLeagueMatch.getMatchRound())
                .matchId(matchDetails.getMatch().getMatchId())
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
    }
}
