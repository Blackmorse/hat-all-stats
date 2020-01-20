package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.api.matchdetails.model.HomeTeam;
import com.blackmorse.hattrick.api.matchdetails.model.Match;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.model.LeagueInfo;
import com.blackmorse.hattrick.model.LeagueInfoWithLeagueUnitDetails;
import com.blackmorse.hattrick.model.LeagueInfoWithLeagueUnitId;
import com.blackmorse.hattrick.model.TeamLeague;
import com.blackmorse.hattrick.model.TeamLeagueMatch;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class HattrickService {
    private final Hattrick hattrick;
    private final Scheduler scheduler;

    private AtomicLong teams = new AtomicLong();
    private AtomicLong matchDetailsCount = new AtomicLong();

    @Autowired
    public HattrickService(@Qualifier("apiExecutor") ExecutorService executorService,
                           Hattrick hattrick) {
        this.hattrick = hattrick;
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
    }

    public List<LeagueInfoWithLeagueUnitId> getLeagueUnitsIdsForCountries(List<String> countryNames) {
        return Flowable.fromIterable(hattrick.getWorldDetails().getLeagueList().stream()
                .filter(league -> countryNames.contains(league.getLeagueName())).collect(Collectors.toList()))

                .map(league ->
                        new LeagueInfo(league.getLeagueId(), league.getSeriesMatchDate(), league.getMatchRound(),
                                league.getSeasonOffset(), league.getSeason() - league.getSeasonOffset()))

                .map(leagueInfo -> new LeagueInfoWithLeagueUnitDetails(leagueInfo, hattrick.getLeagueUnitByName(leagueInfo.getLeagueId(), "II.1")))
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueInfoWithTwoOneDetails -> getAllLeagueIds(leagueInfoWithTwoOneDetails.getLeagueInfo(), leagueInfoWithTwoOneDetails.getLeagueDetails()))

                .flatMap(Flowable::fromIterable)
                .sequential()
                .toList()
                .blockingGet();
    }

    private Flowable<List<LeagueInfoWithLeagueUnitId>> getAllLeagueIds(LeagueInfo leagueInfo, LeagueDetails twoOne) {

        return Flowable.just(Collections.singletonList(new LeagueInfoWithLeagueUnitId(leagueInfo, twoOne.getLeagueLevelUnitId() - 1)))
                .concatWith(Flowable.fromIterable(IntStream.range(2, twoOne.getMaxLevel() + 1)
                        .mapToObj(level -> hattrick.getLeagueUnitIdsForLevel(twoOne.getLeagueId(), level)
                                .stream().map(id -> new LeagueInfoWithLeagueUnitId(leagueInfo, id))
                                .collect(Collectors.toList())).collect(Collectors.toList())));
    }

    public List<TeamLeague> getNonBotTeamsFromLeagueUnitIds(List<LeagueInfoWithLeagueUnitId> leagueUnitIds) {
        return Flowable.fromIterable(leagueUnitIds)
                .parallel()
                .runOn(scheduler)
                .map(leagueInfoWithLeagueUnitId -> new LeagueInfoWithLeagueUnitDetails(leagueInfoWithLeagueUnitId.getLeagueInfo(), hattrick.getLeagueUnitById(leagueInfoWithLeagueUnitId.getLeagueUnitId())))
                .flatMap(this::teamsFromLeague)
                .sequential()
                .toList()
                .blockingGet();
    }

    private Flowable<TeamLeague> teamsFromLeague(LeagueInfoWithLeagueUnitDetails info) {
        return Flowable.fromIterable(
                info.getLeagueDetails().getTeams().
                        stream().
                        filter(team -> team.getUserId() != 0L)
                        .map(team -> {
                            log.info("processing {} team", teams.incrementAndGet());

                            return TeamLeague.builder()
                                    .leagueId(info.getLeagueDetails().getLeagueId())
                                    .leagueLevel(info.getLeagueDetails().getLeagueLevel())
                                    .leagueLevelUnitId(info.getLeagueDetails().getLeagueLevelUnitId())
                                    .leagueUnitName(info.getLeagueDetails().getLeagueLevelUnitName())
                                    .nextMatchRound(info.getLeagueInfo().getNextRound())
                                    .nextRoundDate(info.getLeagueInfo().getNextLeagueMatch())
                                    .teamId(team.getTeamId())
                                    .teamName(team.getTeamName())
                                    .seasonOffset(info.getLeagueInfo().getSeasonOffset())
                                    .currentSeason(info.getLeagueInfo().getCurrentSeason())
                                    .build();
                        })
                        .collect(Collectors.toList())
        );
    }

    public MatchDetails matchDetailsFromMatch(TeamLeagueMatch teamLeagueMatch) {
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
