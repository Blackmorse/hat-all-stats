package com.blackmorse.hattrick.promotions.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PromoteType {
    AUTO(0, "auto"),
    QUALIFY(1, "qualify");

    private int value;
    private String stringValue;
}
