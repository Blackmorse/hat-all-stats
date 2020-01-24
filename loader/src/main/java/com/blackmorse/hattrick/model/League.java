package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class League {
    private final Integer id;
    private final Integer seasonOffset;
    private final Integer nextRound;
    private final Integer maxLevel;
}
