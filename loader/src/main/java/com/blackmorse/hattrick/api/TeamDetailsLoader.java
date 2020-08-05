package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.api.teamdetails.model.Team;
import com.blackmorse.hattrick.api.teamdetails.model.TeamDetails;
import com.blackmorse.hattrick.model.TeamWithMatchAndTeamDetails;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class TeamDetailsLoader {
    private final Scheduler scheduler;
    private final Hattrick hattrick;

    public TeamDetailsLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                         Hattrick hattrick) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
    }

    public List<TeamWithMatchAndTeamDetails> getTeamDetails(java.util.List<TeamWithMatchDetails> teamWithMatchDetails) {
        AtomicLong matchDetailsCounter = new AtomicLong();
        return Flowable.fromIterable(teamWithMatchDetails)
                .parallel()
                .runOn(scheduler)
                .map(teamWithMatchDetail -> {
                    log.debug("Team details: {}", matchDetailsCounter.incrementAndGet());

                    TeamDetails userDetails = hattrick.getTeamDetails(teamWithMatchDetail.getTeamWithMatch().getTeam().getId());

                    Team teamDetails = userDetails.getTeams().stream()
                            .filter(team -> team.getTeamId().equals(teamWithMatchDetail.getTeamWithMatch().getTeam().getId()))
                            .findFirst().get();
                    return new TeamWithMatchAndTeamDetails(teamWithMatchDetail.getTeamWithMatch(),
                            teamDetails);
                })
                .sequential()
                .toList()
                .blockingGet();
    }
}
