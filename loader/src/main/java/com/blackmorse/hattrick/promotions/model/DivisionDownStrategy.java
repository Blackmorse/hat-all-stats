package com.blackmorse.hattrick.promotions.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DivisionDownStrategy {
    private final DownStrategy qualifyPromoteStrategy;
    private final DownStrategy autoPromoteStrategy;
}
