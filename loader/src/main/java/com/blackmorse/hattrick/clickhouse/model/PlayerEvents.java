package com.blackmorse.hattrick.clickhouse.model;

import lombok.Data;

@Data
public class PlayerEvents {
    private Integer season;
    private Integer round;
    private Long playerId;
    private Integer yellowCards = 0;
    private Integer redCards = 0;
    private Integer goals = 0;
    private Integer injury = 0;
    private Integer leftFieldMinute = -1;

    public PlayerEvents(Integer season, Integer round, Long playerId) {
        this.season = season;
        this.round = round;
        this.playerId = playerId;
    }
}
