package com.blackmorse.hattrick.model;

import com.blackmorse.hattrick.api.players.model.Players;
import lombok.Data;

@Data
public class TeamWithMatchAndPlayers {
    private final TeamWithMatch teamWithMatch;
    private final Players players;
}
