package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.api.players.model.Players;
import com.blackmorse.hattrick.model.TeamWithMatchAndPlayers;
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
public class PlayersLoader {
    private final Scheduler scheduler;
    private final Hattrick hattrick;

    public PlayersLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                         Hattrick hattrick) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
    }

    public List<TeamWithMatchAndPlayers> getPlayersFromTeam(List<TeamWithMatchDetails> teamWithMatchDetails) {
        AtomicLong teamsWithPlayersCounter = new AtomicLong();
        return Flowable.fromIterable(teamWithMatchDetails)
                .parallel()
                .runOn(scheduler)
                .map(teamWithMatchDetail -> {
                    log.debug("Players for teams: {}", teamsWithPlayersCounter.incrementAndGet());
                    Players playersFromTeam = hattrick.getPlayersFromTeam(teamWithMatchDetail.getTeamWithMatch().getTeam().getId());
                    return new TeamWithMatchAndPlayers(teamWithMatchDetail.getTeamWithMatch(),
                            playersFromTeam);
                })
                .sequential()
                .toList()
                .blockingGet();
    }
}
