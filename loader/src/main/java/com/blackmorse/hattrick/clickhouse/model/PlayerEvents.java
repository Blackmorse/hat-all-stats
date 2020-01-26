package com.blackmorse.hattrick.clickhouse.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerEvents {
    private Integer season;
    private Integer round;
    private Long playerId;
    private Integer yellowCards;
    private Integer redCards;
    private Integer goals;
    private Integer injury;
}
